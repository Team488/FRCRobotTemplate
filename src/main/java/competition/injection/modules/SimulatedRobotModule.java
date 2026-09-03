package competition.injection.modules;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.electrical_contract.UnitTestCompetitionContract;
import competition.simulation.BaseSimulator;
import competition.simulation.MapleSimulator;
import competition.simulation.arm.BaseArmSimulator;
import competition.subsystems.arms.BaseArmSubsystem;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@Module
public abstract class SimulatedRobotModule {
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(UnitTestCompetitionContract impl);

    @Binds
    @Singleton
    public abstract XSwerveDriveElectricalContract getSwerveContract(ElectricalContract impl);

    @Binds
    @Singleton
    public abstract BasePoseSubsystem getPoseSubsystem(PoseSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseSwerveDriveSubsystem getSwerveDriveSubsystem(DriveSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseDriveSubsystem getDriveSubsystem(BaseSwerveDriveSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseSimulator getSimulator(MapleSimulator impl);

    @Module
    public class ArmModule {
        @Provides
        public XCANMotorController provideArmMotor(
                ElectricalContract contract,
                MockCANMotorController.MockCANMotorControllerFactory factory) {
            var defaultPid = new XCANMotorControllerPIDProperties();
            return factory.create(
                    contract.getArmMotorInfo(),
                    "ArmSubsystem",
                    "Arm",
                    defaultPid
            );
        }
    }

}
