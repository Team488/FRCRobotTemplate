package competition.subsystems.arms.commands;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import xbot.common.command.BaseMaintainerCommand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.arms.BaseArmSubsystem;

import javax.inject.Inject;

public class BaseArmMaintainer extends BaseMaintainerCommand <Angle, Double> {

    final BaseArmSubsystem baseArm;

    final XXboxController controller;

    @Inject
    public BaseArmMaintainer(PropertyFactory pf,
                                 HumanVsMachineDecider.HumanVsMachineDeciderFactory hvmFactory,
                                 BaseArmSubsystem baseArm, XXboxController controller) {
        super(baseArm,pf,hvmFactory,1,1);

        this.baseArm = baseArm;
        this.controller = controller;


    }

    @Override
    public void initialize() {
        super.initialize();
    }


    @Override
    protected void coastAction() {
        baseArm.setPower(0.0);
    }

    @Override
    protected void calibratedMachineControlAction() {
        baseArm.setAngle(baseArm.targetAngle.in(Units.Degrees));
    }

    @Override
    protected double getErrorMagnitude() {
        Angle currentAngle = baseArm.getCurrentValue();
        Angle targetAngle = baseArm.getTargetValue();
        Angle error = targetAngle.minus(currentAngle);

        return error.in(Units.Degrees);
    }

    @Override
    protected Double getHumanInput() {
        double rawInput = controller.getRawAxis(1);
        double currentDegrees = baseArm.getCurrentValue().in(Units.Degrees);

        if (Math.abs(rawInput) > 0) {
            currentDegrees++;
            baseArm.setTargetValue(Units.Degrees.of(currentDegrees + 1));
        } else {
            baseArm.setTargetValue(Units.Degrees.of(currentDegrees - 1));        }
        return rawInput;

    }


    @Override
    protected double getHumanInputMagnitude() {
        return getHumanInput();
    }
}
