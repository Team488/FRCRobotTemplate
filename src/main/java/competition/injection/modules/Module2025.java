package competition.injection.modules;

import competition.electrical_contract.Contract2025;
import competition.electrical_contract.ElectricalContract;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public abstract class Module2025 {
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(Contract2025 impl);
}