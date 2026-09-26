package competition.simulation.arm;

import competition.subsystems.arms.BaseArmSubsystem;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.littletonrobotics.junction.mechanism.LoggedMechanism2d;
import org.littletonrobotics.junction.mechanism.LoggedMechanismLigament2d;
import org.littletonrobotics.junction.mechanism.LoggedMechanismRoot2d;
import xbot.common.advantage.AKitLogger;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.math.PIDManager;
import xbot.common.properties.DoubleProperty;
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
    final LoggedMechanism2d mech2d;
    final LoggedMechanismLigament2d baseArmLigament;
    final DoubleProperty baseArmLengthMeters;
    final DoubleProperty baseArmLigamentBaseAngleDegrees;
    final LoggedMechanismRoot2d root;

    @Inject
    public BaseArmSimulator(
            BaseArmSubsystem arm,
            PIDManager.PIDManagerFactory pidManager, PropertyFactory pf) {
        this.arm = arm;
        // Cast or access the motor directly from the subsystem
        this.armMotor = (MockCANMotorController) arm.armMotor;
        this.pidManager = pidManager.create("Arm");
        this.mech2d = new LoggedMechanism2d(2, 2);
        this.baseArmLengthMeters = pf.createPersistentProperty("length",1);
        this.baseArmLigamentBaseAngleDegrees = pf.createPersistentProperty("Degree",-145);
        this.baseArmLigament = new LoggedMechanismLigament2d("BaseArm", baseArmLengthMeters.get(), baseArmLigamentBaseAngleDegrees.get(), 4, new Color8Bit(Color.kRed));
        this.root = mech2d.getRoot("ArmRoot", 1.0, 1.0);
        root.append(this.baseArmLigament);
        
    }




    public void update() {
        Angle targetAngle = arm.getTargetValue();
        armMotor.setPositionTarget(targetAngle);
        this.baseArmLigament.setAngle(arm.getCurrentValue().in(Degrees));

        MotorInternalPIDHelper.updateInternalPID(armMotor, pidManager);

        // Basic physics simulation: apply power to change position over time
        double simulatedDelta = armMotor.getPower() * 0.05;
        double currentRotations = armMotor.getPosition().in(Rotations);
        armMotor.setPosition(Rotations.of(currentRotations + simulatedDelta));

        // Record telemetry for AdvantageKit
        aKitLogger.record("SimulatedAngle", arm.getCurrentValue().in(Degrees));
        aKitLogger.record("Mechanism2d", mech2d);

    }


}