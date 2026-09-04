package competition;

import competition.electrical_contract.UnitTestCompetitionContract;
import competition.injection.components.CompetitionTestComponent;
import competition.injection.components.DaggerCompetitionTestComponent;
import xbot.common.injection.BaseWPITest;

public class BaseCompetitionTest extends BaseWPITest{
    @Override
    protected CompetitionTestComponent createDaggerComponent() {
        return DaggerCompetitionTestComponent.factory().create(new UnitTestCompetitionContract());
    }

    @Override
    protected CompetitionTestComponent getInjectorComponent() {
        return (CompetitionTestComponent)super.getInjectorComponent();
    }
}
