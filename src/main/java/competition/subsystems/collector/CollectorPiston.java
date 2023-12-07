package competition.subsystems.collector;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XSolenoid;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CollectorPiston extends BaseSubsystem {

    public XSolenoid collectorSolenoid;

    @Inject
    public CollectorPiston(XSolenoid.XSolenoidFactory xSolenoidFactory) {
        this.collectorSolenoid = xSolenoidFactory.create(2);
    }


    public void extend() {
        collectorSolenoid.setOn(true);
    }
    public void retract() {
        collectorSolenoid.setOn(false);
    }

}
