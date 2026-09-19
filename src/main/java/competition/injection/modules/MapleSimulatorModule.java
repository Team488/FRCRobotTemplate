package competition.injection.modules;

import javax.inject.Singleton;

import competition.simulation.BaseSimulator;
import competition.simulation.MapleSimulator;
import dagger.Binds;
import dagger.Module;

/** Provides the physics-backed simulator used by the simulation component. */
@Module
public abstract class MapleSimulatorModule {
    @Binds
    @Singleton
    public abstract BaseSimulator getSimulator(MapleSimulator impl);
}
