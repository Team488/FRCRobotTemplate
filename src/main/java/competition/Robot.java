
package competition;

import competition.electrical_contract.Contract2023;
import competition.electrical_contract.Contract2025;
import competition.electrical_contract.Contract2026;
import competition.electrical_contract.ElectricalContract;
import competition.electrical_contract.RoboxContract;
import competition.electrical_contract.UnitTestCompetitionContract;
import competition.injection.components.BaseRobotComponent;
import competition.injection.components.DaggerRobotComponent;
import competition.injection.components.DaggerSimulationComponent;
import competition.operator_interface.OperatorInterface;
import competition.simulation.BaseSimulator;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import xbot.common.command.BaseRobot;
import xbot.common.math.FieldPose;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class Robot extends BaseRobot {

    public static final double LOOP_INTERVAL = 0.02;

    BaseSimulator simulator;
    OperatorInterface oi;

    Robot() {
        super(LOOP_INTERVAL);
    }

    @Override
    protected void initializeSystems() {
        super.initializeSystems();
        var component = getInjectorComponent();
        component.subsystemDefaultCommandMap();
        component.operatorCommandMap();
        component.swerveDefaultCommandMap();
        oi = component.operatorInterface();

        if (BaseRobot.isSimulation()) {
            simulator = component.simulator();
        }
    }

    @Override
    protected BaseRobotComponent createDaggerComponent() {
        if (BaseRobot.isSimulation()) {
            return DaggerSimulationComponent.factory().create(new UnitTestCompetitionContract());
        }
        return DaggerRobotComponent.factory().create(selectElectricalContract());
    }

    private ElectricalContract selectElectricalContract() {
        String chosenContract = Preferences.getString("ContractToUse", "Competition");
        ElectricalContract contract = switch (chosenContract) {
            case "Robox" -> new RoboxContract();
            case "2023" -> new Contract2023();
            case "2025" -> new Contract2025();
            default -> {
                // By default, use the newest contract
                yield new Contract2026();
            }
        };

        System.out.println("Using " + contract.getClass().getName());
        return contract;
    }

    public BaseRobotComponent getInjectorComponent() {
        return (BaseRobotComponent)super.getInjectorComponent();
    }

    @Override
    public void simulationInit() {
        super.simulationInit();
        // Automatically enables the robot; remove this line of code if you want the robot
        // to start in a disabled state (as it would on the field). However, this does save you the 
        // hassle of navigating to the DS window and re-enabling the simulated robot.
        DriverStationSim.setEnabled(true);
        //webots.setFieldPoseOffset(getFieldOrigin());
    }

    @SuppressWarnings("unused")
    private FieldPose getFieldOrigin() {
        // Modify this to whatever the simulator coordinates are for the "FRC origin" of the field.
        // From a birds-eye view where your alliance station is at the bottom, this is the bottom-left corner
        // of the field.
        return new FieldPose(
            -2.33*PoseSubsystem.INCHES_IN_A_METER, 
            -4.58*PoseSubsystem.INCHES_IN_A_METER, 
            BasePoseSubsystem.FACING_TOWARDS_DRIVERS
            );
    }

    @Override
    public void simulationPeriodic() {
        super.simulationPeriodic();

        if (simulator != null) {
            simulator.update();
        }
    }

    @Override
    protected void sharedPeriodic() {
        super.sharedPeriodic();

        if (this.oi != null) {
            this.oi.periodic();
        }
    }
}
