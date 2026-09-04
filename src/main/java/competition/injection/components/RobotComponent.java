package competition.injection.components;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.injection.modules.CommonModule;
import dagger.BindsInstance;
import dagger.Component;
import xbot.common.injection.modules.RealControlsModule;
import xbot.common.injection.modules.RealDevicesModule;
import xbot.common.injection.modules.RobotModule;

@Singleton
@Component(modules = {
        RobotModule.class,
        RealDevicesModule.class,
        RealControlsModule.class,
        CommonModule.class
})
public abstract class RobotComponent extends BaseRobotComponent {
    @Component.Factory
    public interface Factory {
        RobotComponent create(@BindsInstance ElectricalContract electricalContract);
    }
}
