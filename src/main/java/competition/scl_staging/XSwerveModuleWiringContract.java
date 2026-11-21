package competition.scl_staging;

import org.wpilib.hardware.bus.CANPort;

import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.PDHPort;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;

/**
 * A swerve electrical contract described as data: per-module wiring plus the motor settings shared by all modules.
 * Implementors fill in the hooks, and the {@code build*} defaults turn them into the device infos that
 * {@link XSwerveDriveElectricalContract} returns.
 *
 * <p>
 * Implementors should still override {@code getDriveMotor}, {@code getSteeringMotor} and {@code getSteeringEncoder} in
 * the concrete robot contract, as one-liners calling {@link #buildDriveMotor}, {@link #buildSteeringMotor} and
 * {@link #buildSteeringEncoder}. The electrical report labels each device with the class that declares its getter
 * ({@code definedIn}), and that label should point at the contract holding the actual wiring.
 *
 * <p>
 * SCL candidate: target package {@code xbot.common.injection.electrical_contract}, either as-is or folded into
 * {@link XSwerveDriveElectricalContract}. See {@code package-info.java}.
 */
public interface XSwerveModuleWiringContract extends XSwerveDriveElectricalContract {

    /** The per-module wiring (CAN IDs, PDH ports) for the given module, or null for an unknown module. */
    SwerveModuleWiring getSwerveModuleWiring(SwerveInstance swerveInstance);

    /** Controller type and output config shared by all drive motors. Called per motor; return a fresh config. */
    SwerveMotorSpec getDriveMotorSpec();

    /** Controller type and output config shared by all steering motors. Called per motor; return a fresh config. */
    SwerveMotorSpec getSteeringMotorSpec();

    /** The device name for the given module's drive motor (also its property/logging prefix). */
    String getDriveControllerName(SwerveInstance swerveInstance);

    /** The device name for the given module's steering motor (also its property/logging prefix). */
    String getSteeringControllerName(SwerveInstance swerveInstance);

    /** The device name for the given module's steering encoder (also its property/logging prefix). */
    String getSteeringEncoderControllerName(SwerveInstance swerveInstance);

    /** The CAN bus all swerve devices sit on. */
    default CANPort getSwerveCanBus() {
        return CANPort.CAN_S0;
    }

    default CANMotorControllerInfo buildDriveMotor(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return buildSwerveMotor(getDriveControllerName(swerveInstance), wiring.driveCanId(), wiring.drivePdh(), getDriveMotorSpec());
    }

    default CANMotorControllerInfo buildSteeringMotor(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return buildSwerveMotor(getSteeringControllerName(swerveInstance), wiring.steeringCanId(), wiring.steeringPdh(), getSteeringMotorSpec());
    }

    default DeviceInfo buildSteeringEncoder(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance)).withCanBus(getSwerveCanBus(), wiring.encoderCanId()).build();
    }

    private CANMotorControllerInfo buildSwerveMotor(String name, int canId, PDHPort pdhPort, SwerveMotorSpec spec) {
        return CANMotorControllerInfo.builder(name).withType(spec.type()).withCanBus(getSwerveCanBus(), canId).withPdhPort(pdhPort)
                .withOutputConfig(spec.outputConfig()).build();
    }
}
