package competition.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

import competition.simulation.BaseSimulator;
import competition.simulation.BasicDrivetrainSimulator;

/** Provides drivetrain motion without a MapleSim dependency. */
@Module
public abstract class BasicSimulatorModule {
    @Binds
    @Singleton
    public abstract BaseSimulator getSimulator(BasicDrivetrainSimulator impl);
}
