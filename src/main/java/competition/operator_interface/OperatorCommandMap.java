package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.pathplanner.lib.auto.AutoBuilder;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.subsystems.autonomous.SetAutonomousCommand;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

@Singleton
public class OperatorCommandMap {
    @Inject
    public OperatorCommandMap() {
    }

    @Inject
    public void setupOperatorCommands(OperatorInterface operatorInterface) { }

    @Inject
    public void setupDriveCommands(OperatorInterface operatorInterface) { }

    @Inject
    public void setupOperatorGamepad(OperatorInterface operatorInterface) { }

    @Inject
    public void setupDebugGamepad(OperatorInterface operatorInterface) {}

    @Inject
    public void setupAutoCommands(Provider<SetAutonomousCommand> setAutonomousCommandProvider) { }

    @Inject
    public void setupSimulatorCommands(ResetSimulatedPoseCommand resetPose) {
        resetPose.includeOnSmartDashboard();
    }

    @Inject
    public void setupTestingCommands() {
    }
}