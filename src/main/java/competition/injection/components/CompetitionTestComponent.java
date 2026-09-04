package competition.injection.components;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.injection.modules.CommonModule;
import dagger.BindsInstance;
import dagger.Component;
import xbot.common.injection.modules.MockControlsModule;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.UnitTestModule;

@Singleton
@Component(modules = {
        UnitTestModule.class,
        MockDevicesModule.class,
        MockControlsModule.class,
        CommonModule.class
})
public abstract class CompetitionTestComponent extends BaseRobotComponent {
    @Component.Factory
    public interface Factory {
        CompetitionTestComponent create(@BindsInstance ElectricalContract electricalContract);
    }
}
