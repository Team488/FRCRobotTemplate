package competition.subsystems.drive.commands;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import xbot.common.command.BaseCommand;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class SwerveDriveWithJoysticksCommand extends BaseCommand {

    OperatorInterface oi;
    DriveSubsystem drive;
    PoseSubsystem pose;

    final DoubleProperty overallDrivingPowerScale;
    final DoubleProperty overallTurningPowerScale;

    @Inject
    public SwerveDriveWithJoysticksCommand(OperatorInterface oi, DriveSubsystem drive, PoseSubsystem pose,
                                           PropertyFactory pf) {
        this.drive = drive;
        this.pose = pose;
        this.oi = oi;
        this.addRequirements(drive);

        this.overallDrivingPowerScale = pf.createPersistentProperty("DrivingPowerScale", 1.0);
        this.overallTurningPowerScale = pf.createPersistentProperty("TurningPowerScale", 1.0);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
    }

    @Override
    public void execute() {
        // Get raw human translate and rotation intents
        XYPair translationIntent = getRawHumanTranslationIntent();
        double rotationIntent = getRawHumanRotationIntent();

        // Normalize and scale translationIntent, to prevent diagonal movement being faster
        if (translationIntent.getMagnitude() > 1) {
            double x = translationIntent.x;
            double y = translationIntent.y;

            // Normalize the intent
            translationIntent = translationIntent.scale(1 / translationIntent.getMagnitude());

            // Scale the intent so that it reflects on how far the joystick is (assuming the values are -1 to 1)
            // (So that it will not always be the same speed as long as magnitude is > 1)
            translationIntent = translationIntent.scale(Math.abs(x), Math.abs(y));
        }

        // Further scale translation and rotation intents if not driving full power
        if (!drive.isUnlockFullDrivePowerActive()) {
            // Scale translationIntent if precision modes active, values from XBot2024 repository
            if (drive.isExtremePrecisionTranslationActive()) {
                translationIntent = translationIntent.scale(0.15);
            } else if (drive.isPrecisionTranslationActive()) {
                translationIntent = translationIntent.scale(0.50);
            }

            if (drive.isPrecisionRotationActive()) {
                rotationIntent *= 0.25;
            }

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

    private XYPair getRawHumanTranslationIntent() {
        double xIntent = MathUtils.deadband(oi.gamepad.getLeftVector().x, 0.15);
        double yIntent = MathUtils.deadband(oi.gamepad.getLeftVector().y, 0.15);

        // We have to rotate -90 degrees to fix some alignment issues
        return new XYPair(xIntent, yIntent).rotate(-90);
    }

    private double getRawHumanRotationIntent() {
        // Deadband is to prevent buggy joysticks/triggers
        double rotateLeftIntent = MathUtils.deadband(oi.gamepad.getLeftTrigger(), 0.15);
        double rotateRightIntent = MathUtils.deadband(oi.gamepad.getRightTrigger(), 0.15);

        // Merge the two trigger values together in case of conflicts
        // Rotate left = positive, right = negative
        return rotateLeftIntent - rotateRightIntent;
    }
}
