package competition;

import xbot.common.injection.BaseWPITest;

import competition.electrical_contract.UnitTestCompetitionContract;
import competition.injection.components.CompetitionTestComponent;
import competition.injection.components.DaggerCompetitionTestComponent;

public class BaseCompetitionTest extends BaseWPITest {
    @Override
    protected CompetitionTestComponent createDaggerComponent() {
        return DaggerCompetitionTestComponent.factory().create(new UnitTestCompetitionContract());
    }

    @Override
    protected CompetitionTestComponent getInjectorComponent() {
        return (CompetitionTestComponent) super.getInjectorComponent();
    }
}
