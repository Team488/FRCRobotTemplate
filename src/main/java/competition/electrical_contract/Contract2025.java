package competition.electrical_contract;

import competition.subsystems.pose.PoseSubsystem;
import org.wpilib.hardware.bus.CANPort;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.units.Units;
import org.wpilib.units.measure.Distance;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.injection.electrical_contract.PowerSource;
import xbot.common.injection.electrical_contract.TalonFxMotorControllerOutputConfig;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.subsystems.vision.CameraCapabilities;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.Set;

import static org.wpilib.units.Units.Amps;
import static org.wpilib.units.Units.Inches;

public class Contract2025 extends ElectricalContract {

    @Inject
    public Contract2025() {
        super(Set.of());
    }

    @Override
    public IMUInfo getIMUInfo() {
        // TODO: VERIFY ON THE ROBOT BEFORE DEPLOYING: FLAT is a provisional SystemCore IMU mounting orientation.
        // Replace this with the actual controller mounting orientation; incorrect orientation corrupts heading/pose.
        return IMUInfo.builder("IMU", XGyro.ImuType.onboard)
                .withDeviceId(1)
                .withMountOrientation(MountOrientation.FLAT)
                .withPowerFrom(PowerSource.RIO)
                .build();
    }

    @Override
    public Distance getRadiusOfRobot() {
        return Units.Inches.of(18);
    }

    TalonFxMotorControllerOutputConfig regularDriveMotorConfig =
            new TalonFxMotorControllerOutputConfig()
                    .withInversionType(CANMotorControllerOutputConfig.InversionType.Normal)
                    .withStatorCurrentLimit(Amps.of(60))
                    .withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake);

    TalonFxMotorControllerOutputConfig invertedDriveMotorConfig =
            new TalonFxMotorControllerOutputConfig()
                    .withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)
                    .withStatorCurrentLimit(Amps.of(80))
                    .withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake);

    @Override
    public boolean isDriveReady() {
        return false;
    }

    @Override
    public boolean areCanCodersReady() {
        return false;
    }

    @Override
    public CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance) {
        return switch (swerveInstance.label()) {
            case "FrontRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 39)
                    .withOutputConfig(regularDriveMotorConfig)
                    .build();
            case "RearRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 31)
                    .withOutputConfig(regularDriveMotorConfig)
                    .build();
            case "RearLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 20)
                    .withOutputConfig(regularDriveMotorConfig)
                    .build();
            case "FrontLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 29)
                    .withOutputConfig(regularDriveMotorConfig)
                    .build();
            default -> null;
        };
    }

    @Override
    public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {

        TalonFxMotorControllerOutputConfig invertedSteeringMotorConfig =
                new TalonFxMotorControllerOutputConfig()
                        .withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)
                        .withStatorCurrentLimit(Amps.of(40))
                        .withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake);

        return switch (swerveInstance.label()) {
            case "FrontRightDrive" -> CANMotorControllerInfo.builder(getSteeringControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 38)
                    .withOutputConfig(invertedSteeringMotorConfig)
                    .build();
            case "RearRightDrive" -> CANMotorControllerInfo.builder(getSteeringControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 30)
                    .withOutputConfig(invertedSteeringMotorConfig)
                    .build();
            case "RearLeftDrive" -> CANMotorControllerInfo.builder(getSteeringControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 21)
                    .withOutputConfig(invertedSteeringMotorConfig)
                    .build();
            case "FrontLeftDrive" -> CANMotorControllerInfo.builder(getSteeringControllerName(swerveInstance))
                    .withType(MotorControllerType.TalonFx)
                    .withCanBus(CANPort.CAN_S0, 28)
                    .withOutputConfig(invertedSteeringMotorConfig)
                    .build();
            default -> null;
        };
    }

    @Override
    public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
        return switch (swerveInstance.label()) {
            case "FrontRightDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 54)
                    .build();
            case "RearRightDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 53)
                    .build();
            case "RearLeftDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 52)
                    .build();
            case "FrontLeftDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 51)
                    .build();
            default -> null;
        };
    }

    @Override
    public Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance) {
        // Update these XYPairs with the swerve module locations!!! (In inches)
        return switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> new Translation2d(Inches.of(12), Inches.of(12));
            case "FrontRightDrive" -> new Translation2d(Inches.of(12), Inches.of(-12));
            case "RearLeftDrive" -> new Translation2d(Inches.of(-12), Inches.of(12));
            case "RearRightDrive" -> new Translation2d(Inches.of(-12), Inches.of(-12));
            default -> new Translation2d(0, 0);
        };
    }

    @Override
    public double getSteeringGearRatio() {
        return 12.1; // Documented value for WCP x2i.
    }

    @Override
    public double getDriveGearRatio() {
        return 6.48; // Documented value for WCP x2i with X3 10t gears.
    }

    private static double frontAprilCameraXDisplacement = 10.14 / PoseSubsystem.INCHES_IN_A_METER;
    private static double frontAprilCameraYDisplacement = 6.535 / PoseSubsystem.INCHES_IN_A_METER;
    private static double frontAprilCameraZDisplacement = 6.7 / PoseSubsystem.INCHES_IN_A_METER;
    private static double frontAprilCameraPitch = Math.toRadians(-21);
    private static double frontAprilCameraYaw = Math.toRadians(0);

    @Override
    public CameraInfo[] getCameraInfo() {
        return new CameraInfo[] {// {};

                new CameraInfo("Apriltag_FrontLeft_Camera",
                        "AprilTagFrontLeft",
                        new Transform3d(new Translation3d(
                                frontAprilCameraXDisplacement,
                                frontAprilCameraYDisplacement,
                                frontAprilCameraZDisplacement),
                                new Rotation3d(0, frontAprilCameraPitch, frontAprilCameraYaw)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),

                new CameraInfo("Apriltag_FrontRight_Camera",
                        "AprilTagFrontRight",
                        new Transform3d(new Translation3d(
                                frontAprilCameraXDisplacement,
                                -frontAprilCameraYDisplacement,
                                frontAprilCameraZDisplacement),
                                new Rotation3d(0, frontAprilCameraPitch, frontAprilCameraYaw)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
                new CameraInfo("Apriltag_Back_Camera",
                        "AprilTagBack",
                        new Transform3d(new Translation3d(
                                -0.55 / PoseSubsystem.INCHES_IN_A_METER,
                                -0.25 / PoseSubsystem.INCHES_IN_A_METER,
                                6.3 / PoseSubsystem.INCHES_IN_A_METER),
                                new Rotation3d(0, Math.toRadians(-14.5), Math.PI)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG),
                        false)
        };
    }
}
