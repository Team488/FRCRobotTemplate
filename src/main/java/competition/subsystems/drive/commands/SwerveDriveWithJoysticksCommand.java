package competition.subsystems.drive.commands;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import xbot.common.command.BaseCommand;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;

import javax.inject.Inject;

public class SwerveDriveWithJoysticksCommand extends BaseCommand {

    OperatorInterface oi;
    DriveSubsystem drive;
    PoseSubsystem pose;

    @Inject
    public SwerveDriveWithJoysticksCommand(OperatorInterface oi, DriveSubsystem drive, PoseSubsystem pose) {
        this.drive = drive;
        this.pose = pose;
        this.oi = oi;
        this.addRequirements(drive);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
    }

    @Override
    public void execute() {
        // Get translate and rotation intents
        XYPair translationIntent = getTranslationIntent();
        double rotationIntent = getRotationIntent();

        // Normalize and scale translationIntent
        if (translationIntent.getMagnitude() > 1) {
            double x = translationIntent.x;
            double y = translationIntent.y;

            // Normalize the intent
            translationIntent.scale(1 / translationIntent.getMagnitude());

            // Scale the intent so that it reflects on how far the joystick is (assuming the values are -1 to 1)
            // (So that it will not always be the same speed as long as magnitude is > 1)
            translationIntent.scale(Math.abs(x), Math.abs(y));
        }

        // Field oriented drive will process the actual swerve movements for us
        drive.fieldOrientedDrive(
                translationIntent,
                rotationIntent,
                pose.getCurrentHeading().getDegrees(),
                new XYPair(0,0)
        );
    }

    private XYPair getTranslationIntent() {
        double xIntent = MathUtils.deadband(oi.gamepad.getLeftVector().x, 0.15);
        double yIntent = MathUtils.deadband(oi.gamepad.getLeftVector().y, 0.15);

        // We have to rotate -90 degrees to fix some alignment issues
        return new XYPair(xIntent, yIntent).rotate(-90);
    }

    private double getRotationIntent() {
        // Deadband is to prevent buggy joysticks/triggers
        double rotateLeftIntent = MathUtils.deadband(oi.gamepad.getLeftTrigger(), 0.15);
        double rotateRightIntent = MathUtils.deadband(oi.gamepad.getRightTrigger(), 0.15);

        // Merge the two trigger values together in case of conflicts
        // Rotate left = positive, right = negative
        return rotateLeftIntent - rotateRightIntent;
    }
}
