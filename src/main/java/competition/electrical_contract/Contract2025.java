package competition.electrical_contract;

import java.util.EnumSet;
import java.util.Set;

import javax.inject.Inject;

import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.units.Units;
import org.wpilib.units.measure.Distance;

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

import competition.scl_staging.SwerveModuleWiring;
import competition.scl_staging.SwerveMotorSpec;
import competition.subsystems.pose.PoseSubsystem;

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
        return IMUInfo.builder("IMU", XGyro.ImuType.onboard).withDeviceId(1).withMountOrientation(MountOrientation.FLAT).withPowerFrom(PowerSource.RIO).build();
    }

    @Override
    public Distance getRadiusOfRobot() {
        return Units.Inches.of(18);
    }

    @Override
    public boolean isDriveReady() {
        return false;
    }

    @Override
    public boolean areCanCodersReady() {
        return false;
    }

    @Override
    public SwerveModuleWiring getSwerveModuleWiring(SwerveInstance swerveInstance) {
        return switch (swerveInstance.label()) {
            // drive CAN, steering CAN, encoder CAN
            case "FrontLeftDrive" -> SwerveModuleWiring.withoutPdh(29, 28, 51);
            case "FrontRightDrive" -> SwerveModuleWiring.withoutPdh(39, 38, 54);
            case "RearLeftDrive" -> SwerveModuleWiring.withoutPdh(20, 21, 52);
            case "RearRightDrive" -> SwerveModuleWiring.withoutPdh(31, 30, 53);
            default -> null;
        };
    }

    @Override
    public SwerveMotorSpec getDriveMotorSpec() {
        return new SwerveMotorSpec(MotorControllerType.TalonFx,
                new TalonFxMotorControllerOutputConfig().withInversionType(CANMotorControllerOutputConfig.InversionType.Normal)
                        .withStatorCurrentLimit(Amps.of(60)).withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake));
    }

    @Override
    public SwerveMotorSpec getSteeringMotorSpec() {
        return new SwerveMotorSpec(MotorControllerType.TalonFx,
                new TalonFxMotorControllerOutputConfig().withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)
                        .withStatorCurrentLimit(Amps.of(40)).withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake));
    }

    @Override
    public CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance) {
        return buildDriveMotor(swerveInstance);
    }

    @Override
    public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {
        return buildSteeringMotor(swerveInstance);
    }

    @Override
    public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
        return buildSteeringEncoder(swerveInstance);
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
        return new CameraInfo[] { // {};

                new CameraInfo("Apriltag_FrontLeft_Camera", "AprilTagFrontLeft",
                        new Transform3d(new Translation3d(frontAprilCameraXDisplacement, frontAprilCameraYDisplacement, frontAprilCameraZDisplacement),
                                new Rotation3d(0, frontAprilCameraPitch, frontAprilCameraYaw)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),

                new CameraInfo("Apriltag_FrontRight_Camera", "AprilTagFrontRight",
                        new Transform3d(new Translation3d(frontAprilCameraXDisplacement, -frontAprilCameraYDisplacement, frontAprilCameraZDisplacement),
                                new Rotation3d(0, frontAprilCameraPitch, frontAprilCameraYaw)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG)),
                new CameraInfo("Apriltag_Back_Camera", "AprilTagBack",
                        new Transform3d(new Translation3d(-0.55 / PoseSubsystem.INCHES_IN_A_METER, -0.25 / PoseSubsystem.INCHES_IN_A_METER,
                                6.3 / PoseSubsystem.INCHES_IN_A_METER), new Rotation3d(0, Math.toRadians(-14.5), Math.PI)),
                        EnumSet.of(CameraCapabilities.APRIL_TAG), false) };
    }
}
