package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Singleton;


import competition.subsystems.collector.commands.EjectCommand;
import competition.subsystems.collector.commands.ExtendCommand;

import competition.subsystems.collector.commands.ExtendIntakeCommand;
import competition.subsystems.collector.commands.IntakeCommand;
import competition.subsystems.collector.commands.RetractCommand;
import competition.subsystems.collector.commands.StopCommand;
import xbot.common.controls.sensors.XXboxController.XboxButton;




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
            ExtendCommand extendCommand,
            RetractCommand retractCommand,
            IntakeCommand intakeCommand,
            EjectCommand ejectCommand,
            StopCommand stopCommand,
            ExtendIntakeCommand extendIntakeCommand)
    {
        resetHeading.setHeadingToApply(90);
        operatorInterface.gamepad.getifAvailable(1).onTrue(resetHeading);

        operatorInterface.gamepad.getXboxButton(XboxButton.A).onTrue(extendCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.A).onFalse(retractCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.B).onTrue(intakeCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.B).onFalse(stopCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.Y).onTrue(ejectCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.Y).onFalse(stopCommand);
        operatorInterface.gamepad.getXboxButton(XboxButton.X).onTrue(extendIntakeCommand);
    }
}
