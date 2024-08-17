package competition.subsystems.drive.commands;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.command.BaseCommand;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;

import javax.inject.Inject;

public class SwerveDriveWithJoysticksCommand extends BaseCommand {

    OperatorInterface oi;
    DriveSubsystem drive;
    PoseSubsystem pose;
    HumanVsMachineDecider hvmDecider;
    HeadingModule headingModule;

    final DoubleProperty overallDrivingPowerScale;
    final DoubleProperty overallTurningPowerScale;
    final DoubleProperty minimumMagnitudeToSnap;

    @Inject
    public SwerveDriveWithJoysticksCommand(
            OperatorInterface oi, DriveSubsystem drive, PoseSubsystem pose, PropertyFactory pf,
            HeadingModuleFactory headingModuleFactory, HumanVsMachineDeciderFactory hvmFactory) {
        pf.setPrefix(this);
        this.drive = drive;
        this.pose = pose;
        this.oi = oi;
        this.hvmDecider = hvmFactory.create(this.getPrefix());
        this.headingModule = headingModuleFactory.create(drive.getRotateToHeadingPid());

        this.overallDrivingPowerScale = pf.createPersistentProperty("DrivingPowerScale", 1.0);
        this.overallTurningPowerScale = pf.createPersistentProperty("TurningPowerScale", 1.0);
        this.minimumMagnitudeToSnap = pf.createPersistentProperty("MinimumMagnitudeToSnap", 0.75);
        this.addRequirements(drive);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        hvmDecider.reset();
        drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
    }

    @Override
    public void execute() {
        // Get raw human translate and rotation intents
        XYPair translationIntent = getRawHumanTranslationIntent();
        double rotationIntent = getRawHumanRotationIntent();

        // Checks snapping to side or other rotation features to get suggested intent
        rotationIntent = getSuggestedRotationIntent(rotationIntent);

        // Process the translation intent
        translationIntent = processTranslationIntent(translationIntent);

        // Further scale translation and rotation intents if not driving full power
        if (!drive.isUnlockFullDrivePowerActive()) {
            translationIntent = translationIntent.scale(overallDrivingPowerScale.get());
            rotationIntent *= overallTurningPowerScale.get();
        }

        // Field oriented drive will process the actual swerve movements for us
        drive.fieldOrientedDrive(
                translationIntent,
                rotationIntent,
                pose.getCurrentHeading().getDegrees(),
                new XYPair(0,0)
        );
    }

    private XYPair processTranslationIntent(XYPair intent) {
        // Process translation: normalize & scale translationIntent, prevent diagonal movement being faster
        // This is needed even if isUnlockFullDrivePowerActive == true
        if (intent.getMagnitude() != 0) {
            double x = intent.x;
            double y = intent.y;

            // Normalize the intent
            intent = intent.scale(1 / intent.getMagnitude());

            // Scale the intent so that it reflects on how far the joystick is (assuming the values are -1 to 1)
            // (So that it will not always be the same speed as long as magnitude is > 1)
            intent = intent.scale(Math.abs(x), Math.abs(y));
        }

        // Further scale translation and rotation intents if not driving full power
        if (!drive.isUnlockFullDrivePowerActive()) {
            // Scale translationIntent if precision modes active, values from XBot2024 repository
            if (drive.isExtremePrecisionTranslationActive()) {
                intent = intent.scale(0.15);
            } else if (drive.isPrecisionTranslationActive()) {
                intent = intent.scale(0.50);
            }
        }
        return intent;
    }

    private double scaleHumanRotationIntent(double intent) {
        if (drive.isPrecisionRotationActive()) {
            intent *= 0.25;
        }
        return intent;
    }

    private XYPair getRawHumanTranslationIntent() {
        double xIntent = MathUtils.deadband(oi.gamepad.getLeftVector().x, 0.15);
        double yIntent = MathUtils.deadband(oi.gamepad.getLeftVector().y, 0.15);

        // We have to rotate -90 degrees to fix some alignment issues
        return new XYPair(xIntent, yIntent).rotate(-90);
    }

    private double getRawHumanRotationIntent() {
        // Deadband is to prevent buggy joysticks/triggers
        double rotateLeftIntent = MathUtils.deadband(oi.gamepad.getLeftTrigger(), 0.005);
        double rotateRightIntent = MathUtils.deadband(oi.gamepad.getRightTrigger(), 0.005);

        // Merge the two trigger values together in case of conflicts
        // Rotate left = positive, right = negative
        return rotateLeftIntent - rotateRightIntent;
    }

    private double getSuggestedRotationIntent(double triggerRotateIntent) {
        double suggestedRotationPower = 0;
        // Firstly, I would like to apologize for the namings of these functions...

        // Checks the right joystick input to see if we want to snap to a certain side
        // Apparently, we need to invert the x input here as it has been inverted for other commands already
        // And of course, we must rotate -90 (similar to how we got raw translation) for default alignment
        XYPair joystickInput = new XYPair(-oi.gamepad.getRightVector().x, oi.gamepad.getRightVector().y).rotate(-90);

        if (joystickInput.getMagnitude() >= minimumMagnitudeToSnap.get()) {
            suggestedRotationPower = evaluateSnappingInput(joystickInput);
        } else if (drive.getLookAtPointActive()) {
            suggestedRotationPower = evaluateLookAtPoint();
        } else if (drive.getStaticHeadingActive()) {
            suggestedRotationPower = evaluateStaticHeading();
        } else {
            suggestedRotationPower = evaluateLastKnownHeading(triggerRotateIntent);
        }
        return suggestedRotationPower;
    }

    private double evaluateSnappingInput(XYPair input) {
        double heading = input.getAngle();

        // Rebound the heading to be within -45 to 315 (diagnal X) then shift to 0 to 360 (for division purposes)
        double reboundedHeading = ContiguousDouble.reboundValue(heading, -45, 315)+45;

        // Get which quadrant our rebounded heading is in
        int quadrant = (int) (reboundedHeading / 90);
        double desiredHeading = switch (quadrant) {
            // Modify here if specific heading for certain quadrant(s)
            default -> quadrant * 90;
        };

        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            desiredHeading += 180;
        }

        // This probably needs to be everywhere (in more places)
        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        } else {
            drive.setDesiredHeading(desiredHeading);
        }
        hvmDecider.reset();
        return headingModule.calculateHeadingPower(desiredHeading);
    }

    private double evaluateLookAtPoint() {
        // Don't think this takes in consideration of height...
        Translation2d target = drive.getLookAtPointTarget();

        Pose2d currentPose = pose.getCurrentPose2d();
        Translation2d currentXY = new Translation2d(currentPose.getX(), currentPose.getY());

        double desiredHeading = currentXY.minus(target).getAngle().getDegrees() + 180;
        drive.setDesiredHeading(desiredHeading); // Does this need a recently heading reset check?
        return headingModule.calculateHeadingPower(desiredHeading); // XBot2024 here uses aggressive PIDs
    }

    private double evaluateStaticHeading() {
        double desiredHeading = drive.getStaticHeadingTarget().getDegrees();
        drive.setDesiredHeading(desiredHeading);
        return headingModule.calculateHeadingPower(desiredHeading);
    }

    private double evaluateLastKnownHeading(double triggerRotateIntent) {
        HumanVsMachineDecider.HumanVsMachineMode recommendedMode = hvmDecider.getRecommendedMode(triggerRotateIntent);

        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        }

        return switch (recommendedMode) {
            case HumanControl -> scaleHumanRotationIntent(triggerRotateIntent);
            case InitializeMachineControl -> {
                drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
                yield 0;
            }
            case MachineControl -> headingModule.calculateHeadingPower(drive.getDesiredHeading());
            default -> 0; // This is also the case for Coast
        };
    }
}
