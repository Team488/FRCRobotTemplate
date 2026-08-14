package competition.simulation.arm;


import competition.injection.components.SimulationComponent;

import competition.subsystems.arms.BaseArmSubsystem;

import edu.wpi.first.units.measure.Angle;

import xbot.common.advantage.AKitLogger;

import xbot.common.controls.actuators.XCANMotorController;

import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;

import xbot.common.math.PIDManager;

import xbot.common.math.PIDManager_Factory;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.MotorInternalPIDHelper;


import javax.inject.Inject;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;


public class baseArmSimulator {

    final BaseArmSubsystem arm;

    final AKitLogger aKitLogger = new AKitLogger("Simulator/Collector/");

    final MockCANMotorController armMotor;

    SimulationComponent sim;


    final PIDManager pidManager;


    private Angle relativeAngle;

    private Angle currentAngle;

    private Angle targetAngle;


    @Inject
    public baseArmSimulator(BaseArmSubsystem arm, PIDManager pidManager, PropertyFactory pf) {

        this.arm = arm;

        this.armMotor = (MockCANMotorController) arm.armMotor;

        this.targetAngle = arm.getTargetValue();

        armMotor.setPositionTarget(targetAngle);
        
        this.pidManager = pidManager;



        


    }


    @Inject

    public void update() {

        MotorInternalPIDHelper.updateInternalPID(armMotor, pidManager);


        relativeAngle = arm.getCurrentValue();

        double simulatedDelta = armMotor.getPower() * 0.05;

        double currentRotations = armMotor.getPosition().in(Rotations);
        armMotor.setPosition(Rotations.of(currentRotations + simulatedDelta));

        aKitLogger.record("SimulatedAngle", arm.getCurrentValue().in(Degrees));


    }


} 