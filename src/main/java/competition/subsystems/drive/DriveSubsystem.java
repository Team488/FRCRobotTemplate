package competition.subsystems.drive;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveDriveKinematics;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.command2.InstantCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseRobot;
import xbot.common.command.DataFrameRegistry;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.math.PIDDefaults;
import xbot.common.math.PIDManager.PIDManagerFactory;
import org.wpilib.tunable.TunableDouble;
import xbot.common.properties.TunableLevel;
import xbot.common.properties.TunableFactory;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

@Singleton
public class DriveSubsystem extends BaseSwerveDriveSubsystem {
    private static Logger log = LogManager.getLogger(DriveSubsystem.class);

    private Translation2d lookAtPointTarget = new Translation2d(); // The target point to look at
    private Rotation2d staticHeadingTarget = new Rotation2d(); // The heading you want to constantly be at
    private boolean lookAtPointActive = false;
    private boolean lookAtPointInverted = false;
    private boolean staticHeadingActive = false;
    private final TunableDouble autoInterstitialDistanceErrorThresholdInMeters;
    private final TunableDouble autoInterstitialRotationErrorThresholdInDegrees;
    private final TunableDouble autoEndDistanceErrorThresholdInMeters;
    private final TunableDouble autoEndRotationErrorThresholdInDegrees;
    private final TunableDouble maxAutoTargetSpeedMps;
    private final TunableDouble maxAutoFuelIntakeTargetSpeedMps;
    private final TunableDouble interstitialSpeedMps;

    @Inject
    public DriveSubsystem(PIDManagerFactory pidFactory, TunableFactory pf,
                          @FrontLeftDrive SwerveComponent frontLeftSwerve, @FrontRightDrive SwerveComponent frontRightSwerve,
                          @RearLeftDrive SwerveComponent rearLeftSwerve, @RearRightDrive SwerveComponent rearRightSwerve,
                          DataFrameRegistry dataFrameRegistry) {

        super(pidFactory, pf, frontLeftSwerve, frontRightSwerve, rearLeftSwerve, rearRightSwerve, dataFrameRegistry);
        log.info("Creating DriveSubsystem");

        pf.setPrefix(this.getPrefix());
        pf.setDefaultLevel(TunableLevel.Important);
        this.maxAutoTargetSpeedMps = pf.createDouble("MaxAutoTargetSpeedMetersPerSecond", 2.0);
        this.maxAutoFuelIntakeTargetSpeedMps = pf.createDouble("MaxAutoFuelIntakeTargetSpeedMetersPerSecond", 1.0);
        this.interstitialSpeedMps = pf.createDouble("InterstitialSpeedMetersPerSecond", 0.4);
        this.autoInterstitialDistanceErrorThresholdInMeters = pf.createDouble("autoInterstitialDistanceErrorThresholdInMeters", 0.4);
        this.autoInterstitialRotationErrorThresholdInDegrees = pf.createDouble("autoInterstitialRotationErrorThresholdInDegrees", 10.0);
        this.autoEndDistanceErrorThresholdInMeters = pf.createDouble("autoEndDistanceErrorThresholdInMeters", 0.25);
        this.autoEndRotationErrorThresholdInDegrees = pf.createDouble("autoEndRotationErrorThresholdInDegrees", 5.0);
    }

    @Override
    protected PIDDefaults getPositionalPIDDefaults() {
        return new PIDDefaults(
                1.08, // P
                0, // I
                4.0, // D
                0.0, // F
                0.6, // Max output
                -0.6, // Min output
                0.05, // Error threshold
                0.005, // Derivative threshold
                0.2); // Time threshold
    }

    @Override
    protected PIDDefaults getHeadingPIDDefaults() {
        var errorThreshold = BaseRobot.isSimulation() ? 5.0 : 2.0;
        return new PIDDefaults(
                0.008, // P
                0.0005, // I
                0.01, // D
                0.0, // F
                0.75, // Max output
                -0.75, // Min output
                errorThreshold, // Error threshold
                0.2, // Derivative threshold
                0.2, // Time threshold
                10); // IZone
    }

    public Translation2d getLookAtPointTarget() {
        return lookAtPointTarget;
    }

    public Rotation2d getStaticHeadingTarget() {
        return staticHeadingTarget;
    }

    public boolean getLookAtPointActive() {
        return lookAtPointActive;
    }

    public boolean getStaticHeadingActive() {
        return staticHeadingActive;
    }

    public void setStaticHeadingTarget(Rotation2d staticHeadingTarget) {
        this.staticHeadingTarget = staticHeadingTarget;
    }

    public void setLookAtPointTarget(Translation2d lookAtPointTarget) {
        this.lookAtPointTarget = lookAtPointTarget;
    }

    public void setStaticHeadingTargetActive(boolean staticHeadingActive) {
        this.staticHeadingActive = staticHeadingActive;
    }

    public void setLookAtPointTargetActive(boolean lookAtPointActive) {
        this.lookAtPointActive = lookAtPointActive;
    }

    public void setLookAtPointInverted(boolean lookAtPointInverted) {
        this.lookAtPointInverted = lookAtPointInverted;
    }

    public boolean getLookAtPointInverted() {
        return lookAtPointInverted;
    }

    public double getMaxAutoTargetSpeedMetersPerSecond() {
        return this.maxAutoTargetSpeedMps.get();
    }

    public double getMaxAutoFuelIntakeTargetSpeedMetersPerSecond() {
        return this.maxAutoFuelIntakeTargetSpeedMps.get();
    }

    public double getInterstitialSpeedMetersPerSecond() {
        return this.interstitialSpeedMps.get();
    }

    public double getAutoInterstitialDistanceErrorThresholdInMeters() {
        return this.autoInterstitialDistanceErrorThresholdInMeters.get();
    }

    public double getAutoInterstitialRotationErrorThresholdInDegrees() {
        return this.autoInterstitialRotationErrorThresholdInDegrees.get();
    }

    public double getAutoEndDistanceErrorThresholdInMeters() {
        return this.autoEndDistanceErrorThresholdInMeters.get();
    }

    public double getAutoEndRotationErrorThresholdInDegrees() {
        return this.autoEndRotationErrorThresholdInDegrees.get();
    }

    public InstantCommand createSetStaticHeadingTargetCommand(Supplier<Rotation2d> staticHeadingTarget) {
        return new InstantCommand(() -> {
            setStaticHeadingTarget(staticHeadingTarget.get());
            setStaticHeadingTargetActive(true);
        }
        );
    }

    public InstantCommand createSetLookAtPointTargetCommand(Supplier<Translation2d> lookAtPointTarget) {
        return new InstantCommand(() -> {
            setLookAtPointTarget(lookAtPointTarget.get());
            setLookAtPointTargetActive(true);
        }
        );
    }

    public InstantCommand createClearAllHeadingTargetsCommand() {
        return new InstantCommand(() -> {
            setStaticHeadingTargetActive(false);
            setLookAtPointTargetActive(false);
        });
    }

    /** The follow methods are stole directly from Junjie's SCL PR which is probably 99.5% AI Generated */

    /**
     * Gets the current robot-relative chassis speeds by converting the current swerve module states
     * through inverse kinematics. This is needed by PathPlanner's AutoBuilder.
     *
     * @return The current robot-relative ChassisVelocities.
     */
    public ChassisVelocities getRobotRelativeSpeeds() {
        var states = getCurrentSwerveStates();
        return getSwerveDriveKinematics().toChassisVelocities(states.toArray());
    }

    /**
     * Drives the robot using the given robot-relative ChassisVelocities. Converts the ChassisVelocities
     * to individual swerve module states and applies them. This is needed by PathPlanner's AutoBuilder.
     *
     * @param chassisSpeeds The desired robot-relative chassis speeds.
     */
    public void driveWithChassisVelocities(ChassisVelocities chassisSpeeds) {
        SwerveModuleVelocity[] moduleStates = getSwerveDriveKinematics().toSwerveModuleVelocities(chassisSpeeds);
        moduleStates = SwerveDriveKinematics.desaturateWheelVelocities(moduleStates, getMaxTargetSpeedMetersPerSecond());

        aKitLog.record("DesiredSwerveState", moduleStates);
        this.getFrontLeftSwerveModuleSubsystem().setTargetState(moduleStates[0]);
        this.getFrontRightSwerveModuleSubsystem().setTargetState(moduleStates[1]);
        this.getRearLeftSwerveModuleSubsystem().setTargetState(moduleStates[2]);
        this.getRearRightSwerveModuleSubsystem().setTargetState(moduleStates[3]);
    }
}
