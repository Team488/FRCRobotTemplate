package competition.injection.modules;

import competition.electrical_contract.ElectricalContract;
import competition.electrical_contract.RoboxContract;
import competition.simulation.BaseSimulator;
import competition.simulation.NoopSimulator;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import dagger.Binds;
import dagger.Module;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

import javax.inject.Singleton;

@Module
public abstract class RoboxModule {
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(RoboxContract impl);
}
