package competition.scl_staging;

import xbot.common.injection.electrical_contract.PDHPort;

/**
 * The per-module wiring of one swerve module: the only swerve values that differ between modules. Everything shared
 * (controller type, output config, CAN bus, naming) comes from the contract's other swerve hooks; see
 * {@link XSwerveModuleWiringContract#getSwerveModuleWiring}.
 *
 * @param driveCanId
 *            CAN ID of the drive motor controller.
 * @param drivePdh
 *            PDH port powering the drive motor, or null if not tracked.
 * @param steeringCanId
 *            CAN ID of the steering motor controller.
 * @param steeringPdh
 *            PDH port powering the steering motor, or null if not tracked.
 * @param encoderCanId
 *            CAN ID of the absolute steering encoder.
 */
public record SwerveModuleWiring(int driveCanId, PDHPort drivePdh, int steeringCanId, PDHPort steeringPdh, int encoderCanId) {

    /** Wiring for a robot whose PDH ports aren't recorded in the contract. */
    public static SwerveModuleWiring withoutPdh(int driveCanId, int steeringCanId, int encoderCanId) {
        return new SwerveModuleWiring(driveCanId, null, steeringCanId, null, encoderCanId);
    }
}
