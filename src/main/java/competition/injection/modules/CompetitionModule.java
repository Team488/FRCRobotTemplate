package competition.injection.modules;

import javax.inject.Singleton;

import competition.electrical_contract.Contract2026;
import competition.electrical_contract.ElectricalContract;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class CompetitionModule {
    // Replace Contract2026 with current year's contract
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(Contract2026 impl);
}
