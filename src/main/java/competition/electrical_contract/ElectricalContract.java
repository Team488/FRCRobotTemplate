package competition.electrical_contract;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.injection.electrical_contract.XElectricalReportContract;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;

/*
 * The electrical contract hierarchy:
 *
 * ElectricalContract
 * ├── Contract2023
 * ├── Contract2025
 * └── Contract2026
 *     ├── RoboxContract
 *     └── UnitTestCompetitionContract
 *
 * ElectricalContract defines the shared API and readiness checks for
 * year-specific mechanisms such as arms, elevators, shooters, and intakes.
 *
 * Contract20XX classes provide each year's concrete/evergreen robot configuration,
 * including drivetrain motors, steering encoders, cameras, geometry.
 *
 * Specialized contracts (RoboxContract, etc.) extend the closest matching year and
 * override the hardware or behavior that differs.
 */
/*
 * Electrical report (./gradlew electricalReport, see electrical.report.Main): tag each
 * mechanism getter with the Hardware it belongs to, so robots without that mechanism leave
 * it out of their report and the electrical web tool:
 *
 *     @ReadyWhen("Shooter")
 *     public abstract CANMotorControllerInfo getShooterMotor();
 *
 * Wiring the robot code doesn't use (CAN daisy-chain order, non-motor PDH loads, converter
 * branches) goes in the year's contract by overriding the XElectricalReportContract methods.
 */
public abstract class ElectricalContract implements XSwerveDriveElectricalContract, XCameraElectricalContract, XElectricalReportContract {
    private final Set<Hardware> readinessSet;

    protected ElectricalContract(Set<Hardware> readinessSet) {
        this.readinessSet = readinessSet;
    }

    public boolean isReady(Hardware hardware) {
        return readinessSet.contains(hardware);
    }

    @Override
    public boolean isReady(String hardwareName) {
        return readinessSet.stream().anyMatch(h -> h.name().equals(hardwareName));
    }

    @Override
    public Set<String> getHardwareNames() {
        return Arrays.stream(Hardware.values()).map(Enum::name).collect(Collectors.toSet());
    }

    // TODO: Remove these later
    public abstract boolean isDriveReady();

    public abstract boolean areCanCodersReady();

    public abstract CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance);

    public abstract CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance);

    public abstract DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance);

    public abstract Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance);

    public abstract IMUInfo getIMUInfo();

    public abstract Distance getRadiusOfRobot();

    protected String getDriveControllerName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/Drive";
    }

    protected String getSteeringControllerName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/Steering";
    }

    protected String getSteeringEncoderControllerName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/SteeringEncoder";
    }
}
