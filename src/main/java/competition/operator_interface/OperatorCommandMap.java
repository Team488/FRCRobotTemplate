package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Singleton;

import competition.subsystems.shooter.ShooterSubsystem;
import competition.subsystems.shooter.commands.DecrasingMotorCommand;
import competition.subsystems.shooter.commands.IncreasingMotorCommand;
import competition.subsystems.shooter.commands.StartMotorCommand;
import competition.subsystems.shooter.commands.StopMotorCommand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

/**
 * Maps operator interface buttons to commands
 */
@Singleton
public class OperatorCommandMap {

    @Inject
    public OperatorCommandMap() {

    }

    public void setUpGamePad(OperatorInterface oi,
                             ShooterSubsystem shooterSubsystem,
                             StartMotorCommand startMotorCommand,
                             StopMotorCommand stopMotorCommand,
                             IncreasingMotorCommand increasingMotorCommand,
                             DecrasingMotorCommand decreasingMotorCommand) {
        oi.gamepad.getXboxButton(XXboxController.XboxButton.Start).onTrue(startMotorCommand);
        oi.gamepad.getXboxButton(XXboxController.XboxButton.Back).onTrue(stopMotorCommand);
        oi.gamepad.getXboxButton(XXboxController.XboxButton.LeftTrigger).whileTrue(increasingMotorCommand);
        oi.gamepad.getXboxButton(XXboxController.XboxButton.RightTrigger).whileTrue(decreasingMotorCommand);


    }

    
    // Example for setting up a command to fire when a button is pressed:
    @Inject
    public void setupMyCommands(
            OperatorInterface operatorInterface,
            SetRobotHeadingCommand resetHeading) {
        resetHeading.setHeadingToApply(0);
        operatorInterface.gamepad.getifAvailable(1).onTrue(resetHeading);
    }
}
