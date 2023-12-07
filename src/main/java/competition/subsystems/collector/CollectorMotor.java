package competition.subsystems.collector;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CollectorMotor extends BaseSubsystem {
    public XCANSparkMax collectorMotor;
    public double duration;
    public DoubleProperty currentTime;
    public DoubleProperty durationData;

    /*
    step 1: (Motor) Subsystem
        Forward Command
        Back Command
        Stop Command
    step 2: combine (motor with piston)
     */

    public enum IntakeState {
        intaking,
        ejecting,
        stopped
    }
    IntakeState intakeState = IntakeState.stopped;

    @Inject
    public CollectorMotor(XCANSparkMax.XCANSparkMaxFactory xcanSparkMaxFactory, PropertyFactory propertyFactory){
        this.collectorMotor = xcanSparkMaxFactory.create(new DeviceInfo(25, true), getPrefix(), "CollectorMotor");
        propertyFactory.setPrefix(this);

        currentTime = propertyFactory.createEphemeralProperty("currentTime: ", 0);
        durationData = propertyFactory.createEphemeralProperty("duration: ", 0);

    }

    public void intake() {
        collectorMotor.set(0.1);
        intakeState = IntakeState.intaking;
    }

    public void eject() {
        collectorMotor.set(-0.1);
        intakeState = IntakeState.ejecting;
    }
    public void stop() {
        collectorMotor.set(0);
        intakeState = IntakeState.stopped;
    }
    public void intakeSeconds() {
        intake();
        if(XTimer.getFPGATimestamp() >= duration) {
            stop();
        }
    }

    public void setTime(double seconds) {
        duration = XTimer.getFPGATimestamp() + seconds;
    }
    public IntakeState getIntakeState() {
        return intakeState;
    }

    public void periodic() {
        currentTime.set(XTimer.getFPGATimestamp());
        durationData.set(duration);
    }


}
