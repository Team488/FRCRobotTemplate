package competition.subsystems.arms;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Alert;
import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class BaseArmSubsystem extends BaseSetpointSubsystem <Angle, Double> {
    public final XCANMotorController armMotor;

    private DoubleProperty calibrationValue;

    private Angle currentArmAngle;
    public Angle targetAngle;

    boolean isCalibrated = false;
    final Alert isNotCalibratedAlert = new Alert("CoralArm: not calibrated", Alert.AlertType.kWarning);

    private final int currentLimit = 20;

    private ArmState currentState = ArmState.STOWED;




    @Inject
    public BaseArmSubsystem(XCANMotorController armMotor, PropertyFactory propertyFactory) {
        this.armMotor = armMotor;

        armMotor.setPower(0);

        this.calibrationValue = propertyFactory.createPersistentProperty("Calibration value",.05);

    }


    @Override
    public Angle getTargetValue() {
        return targetAngle;
    }


    public void setTargetValue (Angle desiredAngle) {
        targetAngle = desiredAngle;
        armMotor.setPositionTarget(targetAngle);
    }

    @Override
    public void setPower(Double power) {
        armMotor.setPower(power);
    }


    @Override
    public boolean isCalibrated() {

        if (armMotor.getPower() < .005) {
            armMotor.setPower(0.0);
            return true;
        }

        return false;

    }

    @Override
    protected boolean areTwoTargetsEquivalent(Angle target1, Angle target2) {
        return areTwoDoublesEquivalent(target1.in(Units.Degrees), target2.in(Units.Degrees));
    }

    public enum ArmState {
        STOWED,
        FLAT,
        EXTENDED
    }

    public void periodic() {
        super.periodic();

        currentArmAngle = armMotor.getPosition();

        isNotCalibratedAlert.set(!isCalibrated());

    }

    public void setArmState(ArmState newState) {

        this.currentState = newState;
        switch (currentState) {
            case FLAT:
                targetAngle= Units.Degrees.of(0);
                break;
            case EXTENDED:
                targetAngle= Units.Degrees.of(180);
                break;
            default: // STOWED
                targetAngle = Units.Degrees.of(45);
        }
    }

    public Angle getCurrentValue(){
        currentArmAngle = armMotor.getPosition();
        return currentArmAngle;
    }

    public void setAngle(double degrees){
        currentArmAngle = Units.Degrees.of(degrees);
    }


}
