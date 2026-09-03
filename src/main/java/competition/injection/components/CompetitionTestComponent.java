package competition.injection.components;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.injection.modules.CommonModule;
import competition.injection.modules.UnitTestRobotModule;
import dagger.Component;
import dagger.Provides;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.modules.MockControlsModule;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.UnitTestModule;

@Singleton
@Component(modules = { UnitTestModule.class, MockDevicesModule.class, MockControlsModule.class,
        CommonModule.class, UnitTestRobotModule.class})
public abstract class CompetitionTestComponent extends BaseRobotComponent {


    
}
