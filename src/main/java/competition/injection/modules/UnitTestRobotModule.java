package competition.injection.modules;

import competition.electrical_contract.ElectricalContract;
import competition.electrical_contract.UnitTestCompetitionContract;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public abstract class UnitTestRobotModule {
    @Binds
    @Singleton
    public abstract ElectricalContract getElectricalContract(UnitTestCompetitionContract impl);
}
