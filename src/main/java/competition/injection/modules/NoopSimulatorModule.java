package competition.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

import competition.simulation.BaseSimulator;
import competition.simulation.NoopSimulator;

/** Provides a safe simulator placeholder where physics simulation is not run. */
@Module
public abstract class NoopSimulatorModule {
    @Binds
    @Singleton
    public abstract BaseSimulator getSimulator(NoopSimulator impl);
}
