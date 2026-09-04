package competition.injection.modules;

import competition.electrical_contract.ElectricalContract;
import competition.electrical_contract.RoboxContract;
import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
public abstract class RoboxModule {
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(RoboxContract impl);
}
