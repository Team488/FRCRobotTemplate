package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Singleton;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

/**
 * Maps operator interface buttons to commands
 */
@Singleton
public class OperatorCommandMap {

    @Inject
    public OperatorCommandMap() {}
    
    // Example for setting up a command to fire when a button is pressed:
    @Inject
    public void setupMyCommands(
            OperatorInterface operatorInterface,
            SetRobotHeadingCommand resetHeading,
            DriveSubsystem drive) {
        resetHeading.setHeadingToApply(90);
        operatorInterface.gamepad.getifAvailable(1).onTrue(resetHeading);

        // Stolen from 2024
        Translation2d pointAtTestTranslation = new Translation2d(2,2);
        Rotation2d staticHeadingTestRotation = Rotation2d.fromDegrees(-45);

        var pointAtSpeaker = drive.createSetLookAtPointTargetCommand(
                () -> PoseSubsystem.convertBlueToRedIfNeeded(pointAtTestTranslation));

        var pointAtSource = drive.createSetStaticHeadingTargetCommand(
                () -> PoseSubsystem.convertBlueToRedIfNeeded(staticHeadingTestRotation));

        var cancelSpecialPointAtPosition = drive.createClearAllHeadingTargetsCommand();

        operatorInterface.gamepad.getXboxButton(XXboxController.XboxButton.B)
                .onTrue(pointAtSpeaker)
                .onFalse(cancelSpecialPointAtPosition);
        operatorInterface.gamepad.getXboxButton(XXboxController.XboxButton.Y)
                .onTrue(pointAtSource)
                .onFalse(cancelSpecialPointAtPosition);
    }
}
