package competition.subsystems.shooter;

import competition.electrical_contract.ElectricalContract;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.AngularVelocity;
import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.injection.electrical_contract.   CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.math.PIDManager;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShooterSubsystem extends BaseSubsystem {
    final DoubleProperty upperRpm;
    final DoubleProperty lowerRpm;
    final DoubleProperty startingRpm1;
    final DoubleProperty startingRpm2;
    final DoubleProperty maxRpm;
    final DoubleProperty minRpm;
    final DoubleProperty rpmIncrease;
    final DoubleProperty rpmDecrease;



    final XCANMotorController shooter1;
    final XCANMotorController shooter2;

    final ElectricalContract el;



    @Inject
    public ShooterSubsystem(PropertyFactory pf, ElectricalContract el,
                            XCANMotorController.XCANMotorControllerFactory xcanMotorControllerFactory) {
        this.el = el;
        pf.setPrefix(this);
        upperRpm = pf.createPersistentProperty("UpperRPM", 0);
        lowerRpm = pf.createPersistentProperty("LowerRPM", 0);
        startingRpm1 = pf.createPersistentProperty("Starting rpm 1",200);
        startingRpm2 = pf.createPersistentProperty("Starting rpm 2",200);
        minRpm = pf.createPersistentProperty("Minimum rpm", 0);
        maxRpm = pf.createPersistentProperty("Max rpm limit", 4000);
        rpmIncrease = pf.createPersistentProperty("rpmIncreaseRate", 1.1); //Increase rpm by 10%
        rpmDecrease = pf.createPersistentProperty("rpmDecreaseRate", .9); //Increase rpm by 10%



        shooter1 = xcanMotorControllerFactory.create(new CANMotorControllerInfo("Shooter1",
                MotorControllerType.SparkMax,
                CANBusId.RIO,
                32,
                new CANMotorControllerOutputConfig()
                        .withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)), getPrefix(), "PID");
        //shooter2 = xcanMotorControllerFactory.create(xcanMotorController, "2", "2'");
        shooter2 = xcanMotorControllerFactory.create(new CANMotorControllerInfo("Shooter2",
                MotorControllerType.SparkMax,
                CANBusId.RIO,
                36,
                new CANMotorControllerOutputConfig()
                        .withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)), getPrefix(), "PID");

        shooter1.setPidProperties(
                0.01,
                0,
                0);
        shooter2.setPidProperties(
                0.01,
                0,
                0);
    }

   // public boolean isShooterMotorReady(){

    //}

    public void stopMotors(){
        shooter1.setPower(0);
        shooter2.setPower(0);

    }



    public void startMotor(){
        shooter1.setPower(1);
        shooter2.setPower(1);
        //shooter1.setVelocityTarget(Units.RPM.of(startingRpm1.get()));
        //shooter2.setVelocityTarget(Units.RPM.of(startingRpm2.get()));
    }

    public void increaseMotorRpm(){
        shooter1.setVelocityTarget(Units.RPM.of(startingRpm1.get()).times(rpmIncrease.get()));
        shooter2.setVelocityTarget(Units.RPM.of(startingRpm2.get()).times(rpmIncrease.get()));

    }


    public void decreaseMotorRpm(){
        shooter1.setVelocityTarget(Units.RPM.of(startingRpm1.get()).times(rpmDecrease.get()));
        shooter2.setVelocityTarget(Units.RPM.of(startingRpm2.get()).times(rpmDecrease.get()));
    }





}