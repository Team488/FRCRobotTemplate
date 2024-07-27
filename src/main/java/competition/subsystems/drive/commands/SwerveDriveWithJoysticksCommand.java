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
        // Field oriented drive will process most of the hard stuff for us
        drive.fieldOrientedDrive(
                getTranslationIntent(),
                getRotationIntent(),
                pose.getCurrentHeading().getDegrees(),
                new XYPair(0,0)
        );
    }

    private XYPair getTranslationIntent() {
        double xIntent = MathUtils.deadband(oi.gamepad.getLeftVector().x, 0.15);
        double yIntent = MathUtils.deadband(oi.gamepad.getLeftVector().y, 0.15);

        return new XYPair(xIntent, yIntent).rotate(-90);
    }

    private double getRotationIntent() {
        // Deadband is to prevent buggy joysticks
        double rotateLeftIntent = MathUtils.deadband(oi.gamepad.getLeftTrigger(), 0.15);
        double rotateRightIntent = MathUtils.deadband(oi.gamepad.getRightTrigger(), 0.15);

        // Merge the two trigger values together in case of conflicts
        // Rotate left = positive, right = negative
        return rotateLeftIntent - rotateRightIntent;
    }
}
