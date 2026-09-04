package competition.injection.components;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.injection.modules.CommonModule;
import dagger.BindsInstance;
import dagger.Component;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.RealControlsModule;
import xbot.common.injection.modules.SimulationModule;

@Singleton
@Component(modules = {
        SimulationModule.class,
        MockDevicesModule.class,
        RealControlsModule.class,
        CommonModule.class
})
public abstract class SimulationComponent extends BaseRobotComponent {
    @Component.Factory
    public interface Factory {
        SimulationComponent create(@BindsInstance ElectricalContract electricalContract);
    }
}
