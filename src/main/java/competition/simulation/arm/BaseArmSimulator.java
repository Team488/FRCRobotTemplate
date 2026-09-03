package competition.simulation.arm;

import competition.subsystems.arms.BaseArmSubsystem;
import edu.wpi.first.units.measure.Angle;
import xbot.common.advantage.AKitLogger;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.math.PIDManager;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.MotorInternalPIDHelper;

import javax.inject.Inject;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;

public class BaseArmSimulator {

    final BaseArmSubsystem arm;
    final AKitLogger aKitLogger = new AKitLogger("Simulator/Arm/");
    final MockCANMotorController armMotor;
    final PIDManager pidManager;

    @Inject
    public BaseArmSimulator(PIDManager.PIDManagerFactory pidManager, PropertyFactory pf, MockCANMotorController.MockCANMotorControllerFactory armMotor) {
        this.arm = new BaseArmSubsystem(armMotor, pf);
        this.armMotor = (MockCANMotorController) arm.armMotor;
        this.pidManager = pidManager.create("Arm");
    }


    public void update() {
        Angle targetAngle = arm.getTargetValue();
        armMotor.setPositionTarget(targetAngle);


        MotorInternalPIDHelper.updateInternalPID(armMotor, pidManager);

        // Basic physics simulation: apply power to change position over time
        double simulatedDelta = armMotor.getPower() * 0.05;
        double currentRotations = armMotor.getPosition().in(Rotations);
        armMotor.setPosition(Rotations.of(currentRotations + simulatedDelta));

        // Record telemetry for AdvantageKit
        aKitLogger.record("SimulatedAngle", arm.getCurrentValue().in(Degrees));
    }
}