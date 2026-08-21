package competition.subsystems.arms.commands;

import competition.subsystems.arms.BaseArmSubsystem;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import xbot.common.command.BaseMaintainerCommand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.properties.PropertyFactory;

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
        baseArm.setTargetValue(baseArm.targetAngle);
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

        if (Math.abs(rawInput) > 0.05) {
            double currentDegrees = baseArm.getCurrentValue().in(Units.Degrees);
            baseArm.setTargetValue(
                    Units.Degrees.of(currentDegrees + rawInput)
            );
        }
        return rawInput;

    }


    @Override
    protected double getHumanInputMagnitude() {
        return getHumanInput();
    }
}
