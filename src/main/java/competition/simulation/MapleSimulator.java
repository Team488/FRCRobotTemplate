package competition.simulation;

import static edu.wpi.first.units.Units.Seconds;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.SelfControlledSwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;

import competition.Robot;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.Units;
import xbot.common.advantage.AKitLogger;
import xbot.common.controls.sensors.mock_adapters.MockGyro;

import java.util.Arrays;

/**
 * MapleSim 2026 integration; LegacyWpilibAdapter converts values at the WPILib 2027 boundary.
 * SimulationComponent still selects BasicDrivetrainSimulator.
 * Running MapleSim on 2027 requires a compatible runtime, including native libraries.
 */
@Singleton
public class MapleSimulator implements BaseSimulator {
    final PoseSubsystem pose;
    final DriveSubsystem drive;

    protected final AKitLogger aKitLog;

    // maple-sim stuff ----------------------------
    final DriveTrainSimulationConfig config;
    final SelfControlledSwerveDriveSimulation swerveDriveSimulation;

    @Inject
    public MapleSimulator(PoseSubsystem pose, DriveSubsystem drive) {
        this.pose = pose;
        this.drive = drive;

        aKitLog = new AKitLogger("Simulator/");

        @SuppressWarnings("unchecked")
        var ourConfig = new DriveTrainSimulationConfig(
                Units.Kilograms.of(45),
                Units.Meters.of(0.76),
                Units.Meters.of(0.76),
                Units.Meters.of(0.52),
                Units.Meters.of(0.52),
                COTS.ofPigeon2(),
                COTS.ofMark4(
                        DCMotor.getKrakenX60(1),
                        DCMotor.getKrakenX60(1),
                        COTS.WHEELS.SLS_PRINTED_WHEELS.cof,
                        3)
        );

        // TODO: custom things to provide here like motor ratios and what have you
        config = ourConfig.withCustomModuleTranslations(new edu.wpi.first.math.geometry.Translation2d[] {
                LegacyWpilibAdapter.toLegacyTranslation(drive.getFrontLeftSwerveModuleSubsystem().getModuleTranslation()),
                LegacyWpilibAdapter.toLegacyTranslation(drive.getFrontRightSwerveModuleSubsystem().getModuleTranslation()),
                LegacyWpilibAdapter.toLegacyTranslation(drive.getRearLeftSwerveModuleSubsystem().getModuleTranslation()),
                LegacyWpilibAdapter.toLegacyTranslation(drive.getRearRightSwerveModuleSubsystem().getModuleTranslation())
        });

        // starting middle ish of the field on blue
        var startingPose = new Pose2d(7, 7 , new Rotation2d());

        // Creating the SelfControlledSwerveDriveSimulation instance
        this.swerveDriveSimulation = new SelfControlledSwerveDriveSimulation(
                new SwerveDriveSimulation(config, LegacyWpilibAdapter.toLegacyPose(startingPose)));
        SimulatedArena.getInstance().addDriveTrainSimulation(
                swerveDriveSimulation.getDriveTrainSimulation());

        // Tell the robot it's starting in the same spot
        pose.setCurrentPoseInMeters(startingPose);

        // TODO: this should depend on when we actually deploy and run our collector
        // but for now just auto deploy it right away
        SimulatedArena.overrideSimulationTimings(Seconds.of(Robot.LOOP_INTERVAL), 5);
    }

    @Override
    public void update() {
        this.updateDriveSimulation();
    }

    protected void updateDriveSimulation() {
        // drive simulated robot from requested robot commands
        swerveDriveSimulation.runSwerveStates(Arrays.stream(drive.getTargetSwerveStates().toArray())
                .map(LegacyWpilibAdapter::toLegacyState)
                .toArray(edu.wpi.first.math.kinematics.SwerveModuleState[]::new));

        // Advance the physics world, then update MapleSim's odometry from the new sensor values.
        SimulatedArena.getInstance().simulationPeriodic();
        swerveDriveSimulation.periodic();

        // this is where the robot really is in the sim
        aKitLog.record("FieldSimulation/Robot", getGroundTruthPose());

        // tell the pose subsystem about where the robot has moved based on odometry
        pose.ingestSimulatedSwerveModulePositions(Arrays.stream(swerveDriveSimulation.getLatestModulePositions())
                .map(LegacyWpilibAdapter::fromLegacyPosition)
                .toArray(SwerveModulePosition[]::new));

        aKitLog.record("RobotVelocity", LegacyWpilibAdapter.fromLegacyVelocities(
                swerveDriveSimulation.getActualSpeedsFieldRelative()));

        // update gyro reading from sim
        ((MockGyro) pose.imu).setYaw(LegacyWpilibAdapter.fromLegacyHeading(
                this.swerveDriveSimulation.getOdometryEstimatedPose().getRotation()));
    }

    @Override
    public void resetPosition(Pose2d pose) {
        this.swerveDriveSimulation.getDriveTrainSimulation().setSimulationWorldPose(
                LegacyWpilibAdapter.toLegacyPose(pose));
        this.pose.setCurrentPoseInMeters(pose);
    }

    @Override
    public Pose2d getGroundTruthPose() {
        return LegacyWpilibAdapter.fromLegacyPose(this.swerveDriveSimulation.getActualPoseInSimulationWorld());
    }
}
