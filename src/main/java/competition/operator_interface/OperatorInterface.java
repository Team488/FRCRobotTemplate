package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.XXboxController.XXboxControllerFactory;
import xbot.common.logging.RobotAssertionManager;
import org.wpilib.tunable.TunableDouble;
import xbot.common.properties.TunableLevel;
import xbot.common.properties.TunableFactory;

/**
 * This class is the glue that binds the controls on the physical operator interface to the commands and command groups
 * that allow control of the robot.
 */
@Singleton
public class OperatorInterface {
    public XXboxController driverGamepad;
    public XXboxController operatorGamepad;
    public XXboxController setupDebugGamepad;

    final TunableDouble driverDeadband;
    final TunableDouble operatorDeadband;

    @Inject
    public OperatorInterface(XXboxControllerFactory controllerFactory, RobotAssertionManager assertionManager,
                             TunableFactory tf) {
        driverGamepad = controllerFactory.create(0);
        driverGamepad.setLeftInversion(false, true);
        driverGamepad.setRightInversion(true, true);

        operatorGamepad = controllerFactory.create(1);
        operatorGamepad.setLeftInversion(false,true);
        operatorGamepad.setRightInversion(true,true);

        setupDebugGamepad = controllerFactory.create(2);
        setupDebugGamepad.setLeftInversion(false,true);
        setupDebugGamepad.setRightInversion(true,true);

        tf.setPrefix("OperatorInterface");
        tf.setDefaultLevel(TunableLevel.Debug);
        driverDeadband = tf.createDouble("Driver Deadband", 0.12);
        operatorDeadband = tf.createDouble("Operator Deadband", 0.15);
    }

    public double getDriverGamepadTypicalDeadband() {
        return driverDeadband.get();
    }

    public double getOperatorGamepadTypicalDeadband() {
        return operatorDeadband.get();
    }

    public void periodic() {
        driverGamepad.getRumbleManager().periodic();
        operatorGamepad.getRumbleManager().periodic();
    }
}
