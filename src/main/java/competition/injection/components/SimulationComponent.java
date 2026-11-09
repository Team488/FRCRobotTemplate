package competition.injection.components;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.RealControlsModule;
import xbot.common.injection.modules.SimulationModule;

import competition.electrical_contract.ElectricalContract;
import competition.injection.modules.BasicSimulatorModule;
import competition.injection.modules.CommonModule;

@Singleton
@Component(modules = { SimulationModule.class, MockDevicesModule.class, RealControlsModule.class, CommonModule.class, BasicSimulatorModule.class })
public abstract class SimulationComponent extends BaseRobotComponent {
    @Component.Factory
    public interface Factory {
        SimulationComponent create(@BindsInstance ElectricalContract electricalContract);
    }
}
