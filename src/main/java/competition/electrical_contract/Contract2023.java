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
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.injection.electrical_contract.PowerSource;
import xbot.common.injection.electrical_contract.SparkMaxMotorControllerOutputConfig;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.subsystems.vision.CameraCapabilities;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.Set;

import static org.wpilib.units.Units.Amps;
import static org.wpilib.units.Units.Inches;

public class Contract2023 extends ElectricalContract {

    @Inject
    public Contract2023() {
        super(Set.of());
    }

    @Override
    public boolean isDriveReady() {
        return true;
    }

    @Override
    public boolean areCanCodersReady() {
        return true;
    }

    @Override
    public CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance) {
        var driveMotorControllerOutputConfig = new SparkMaxMotorControllerOutputConfig()
                .withSmartCurrentLimit(Amps.of(60));

        return switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 31)
                    .withOutputConfig(driveMotorControllerOutputConfig)
                    .build();
            case "FrontRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 29)
                    .withOutputConfig(driveMotorControllerOutputConfig)
                    .build();
            case "RearLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 38)
                    .withOutputConfig(driveMotorControllerOutputConfig)
                    .build();
            case "RearRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 21)
                    .withOutputConfig(driveMotorControllerOutputConfig)
                    .build();
            default -> null;
        };
    }

    @Override
    public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {
        var steerMotorControllerOutputConfig = new SparkMaxMotorControllerOutputConfig()
                .withSmartCurrentLimit(Amps.of(40));

        return switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 30)
                    .withOutputConfig(steerMotorControllerOutputConfig)
                    .build();
            case "FrontRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 28)
                    .withOutputConfig(steerMotorControllerOutputConfig)
                    .build();
            case "RearLeftDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 39)
                    .withOutputConfig(steerMotorControllerOutputConfig)
                    .build();
            case "RearRightDrive" -> CANMotorControllerInfo.builder(getDriveControllerName(swerveInstance))
                    .withType(MotorControllerType.SparkMax)
                    .withCanBus(CANPort.CAN_S0, 20)
                    .withOutputConfig(steerMotorControllerOutputConfig)
                    .build();
            default -> null;
        };
    }

    @Override
    public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
        return switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 51)
                    .build();
            case "FrontRightDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 52)
                    .build();
            case "RearLeftDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 53)
                    .build();
            case "RearRightDrive" -> DeviceInfo.builder(getSteeringEncoderControllerName(swerveInstance))
                    .withCanBus(CANPort.CAN_S0, 54)
                    .build();
            default -> null;
        };
    }

    @Override
    public Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance) {
        // Update these XYPairs with the swerve module locations!!! (In inches)
        return switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> new Translation2d(Inches.of(15), Inches.of(15));
            case "FrontRightDrive" -> new Translation2d(Inches.of(15), Inches.of(-15));
            case "RearLeftDrive" -> new Translation2d(Inches.of(-15), Inches.of(15));
            case "RearRightDrive" -> new Translation2d(Inches.of(-15), Inches.of(-15));
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

    @Override
    public CameraInfo[] getCameraInfo() {
        double sideAprilCameraXDisplacement = 6.75 / PoseSubsystem.INCHES_IN_A_METER;
        double sideAprilCameraYDisplacement = 7.75 / PoseSubsystem.INCHES_IN_A_METER;
        double sideAprilCameraZDisplacement = 11 / PoseSubsystem.INCHES_IN_A_METER;
        double sideAprilCameraPitch = Math.toRadians(-25);

        return new CameraInfo[]{
                new CameraInfo("Apriltag_Left_Camera",
                        "AprilTagLeft",
                        new Transform3d(new Translation3d(
                                sideAprilCameraXDisplacement,
                                sideAprilCameraYDisplacement,
                                sideAprilCameraZDisplacement),
                                new Rotation3d(0, sideAprilCameraPitch, Math.toRadians(90))),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
                new CameraInfo("Apriltag_Right_Camera",
                        "AprilTagRight",
                        new Transform3d(new Translation3d(
                                -sideAprilCameraXDisplacement,
                                sideAprilCameraYDisplacement,
                                sideAprilCameraZDisplacement),
                                new Rotation3d(0, sideAprilCameraPitch, Math.toRadians(270))),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
                new CameraInfo("Apriltag_Front_Camera",
                        "AprilTagFront",
                        new Transform3d(new Translation3d(
                                0.0,
                                13.125 / PoseSubsystem.INCHES_IN_A_METER,
                                14.0 / PoseSubsystem.INCHES_IN_A_METER),
                                new Rotation3d(0, Math.toRadians(-30), Math.toRadians(0))),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
                new CameraInfo("Apriltag_Back_Camera",
                        "AprilTagBack",
                        new Transform3d(new Translation3d(
                                0,
                                -12.959212 / PoseSubsystem.INCHES_IN_A_METER,
                                17.768664 / PoseSubsystem.INCHES_IN_A_METER),
                                new Rotation3d(0, Math.toRadians(-15), Math.toRadians(180))),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
        };
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

}