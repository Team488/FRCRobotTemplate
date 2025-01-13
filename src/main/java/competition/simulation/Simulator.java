package competition.simulation;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static edu.wpi.first.units.Units.Rotations;

import xbot.common.advantage.AKitLogger;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.controls.sensors.mock_adapters.MockCANCoder;
import xbot.common.controls.sensors.mock_adapters.MockGyro;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.SelfControlledSwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;

@Singleton
public class Simulator implements BaseSimulator{
    final PoseSubsystem pose;
    final DriveSubsystem drive;
    final double robotTopSpeedInMetersPerSecond = 3.0;
    final double robotLoopPeriod = 0.02;
    final double robotTopAngularSpeedInDegreesPerSecond = 360;
    final double poseAdjustmentFactorForDriveSimulation = robotTopSpeedInMetersPerSecond * robotLoopPeriod;
    final double headingAdjustmentFactorForDriveSimulation = robotTopAngularSpeedInDegreesPerSecond * robotLoopPeriod;

    protected final AKitLogger aKitLog;

    // maple-sim stuff
    final DriveTrainSimulationConfig config;
    final SimulatedArena arena;
    final SelfControlledSwerveDriveSimulation swerveDriveSimulation;

    @Inject
    public Simulator(PoseSubsystem pose, DriveSubsystem drive) {
        this.pose = pose;
        this.drive = drive;

        aKitLog = new AKitLogger("Simulator/");

        arena = SimulatedArena.getInstance();
        // more custom things to provide here like motor ratios and what have you
        config = DriveTrainSimulationConfig.Default().withCustomModuleTranslations(new Translation2d[] {
                drive.getFrontLeftSwerveModuleSubsystem().getModuleTranslation(),
                drive.getFrontRightSwerveModuleSubsystem().getModuleTranslation(),
                drive.getRearLeftSwerveModuleSubsystem().getModuleTranslation(),
                drive.getRearRightSwerveModuleSubsystem().getModuleTranslation()
        });
        // middle ish of the field
        var startingPose = new Pose2d(6, 4, new Rotation2d());

        // Creating the SelfControlledSwerveDriveSimulation instance
        this.swerveDriveSimulation = new SelfControlledSwerveDriveSimulation(
                new SwerveDriveSimulation(config, startingPose));
        pose.setCurrentPoseInMeters(startingPose);

        arena.addDriveTrainSimulation(swerveDriveSimulation.getDriveTrainSimulation());
    }

    public void update() {
        // drive simulated robot from requested robot commands
        swerveDriveSimulation.runSwerveStates(new SwerveModuleState[] {
                drive.getFrontLeftSwerveModuleSubsystem().getTargetState(),
                drive.getFrontRightSwerveModuleSubsystem().getTargetState(),
                drive.getRearLeftSwerveModuleSubsystem().getTargetState(),
                drive.getRearRightSwerveModuleSubsystem().getTargetState()
        });

        // run the simulation
        arena.simulationPeriodic();
        swerveDriveSimulation.periodic();

        // read values back out from sim
        aKitLog.record("RobotGroundTruthPose", swerveDriveSimulation.getActualPoseInSimulationWorld());
        aKitLog.record("MapleOdometryPose", swerveDriveSimulation.getOdometryEstimatedPose());

        // we have to divide the sim numbers out because we later will multiply by this factor before giving the values to the estimator
        //pose.mockPositions = swerveDriveSimulation.getLatestModulePositions();
        // var metersPerRotation = drive.getFrontLeftSwerveModuleSubsystem().getDriveSubsystem().metersPerMotorRotation.get();
        // ((MockCANMotorController)drive.getFrontLeftSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
        //     Rotations.of(swerveDriveSimulation.getLatestModulePositions()[0].distanceMeters / metersPerRotation)
        // );
        // ((MockCANMotorController)drive.getFrontRightSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
        //     Rotations.of(swerveDriveSimulation.getLatestModulePositions()[1].distanceMeters/ metersPerRotation)
        // );
        // ((MockCANMotorController)drive.getRearLeftSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
        //     Rotations.of(swerveDriveSimulation.getLatestModulePositions()[2].distanceMeters / metersPerRotation)
        // );
        // ((MockCANMotorController)drive.getRearRightSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
        //     Rotations.of(swerveDriveSimulation.getLatestModulePositions()[3].distanceMeters / metersPerRotation)
        // );

        // ((MockCANCoder)drive.getFrontLeftSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
        //     swerveDriveSimulation.getLatestModulePositions()[0].angle.getDegrees()
        // );
        // ((MockCANCoder)drive.getFrontRightSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
        //     swerveDriveSimulation.getLatestModulePositions()[1].angle.getDegrees()
        // );
        // ((MockCANCoder)drive.getRearLeftSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
        //     swerveDriveSimulation.getLatestModulePositions()[2].angle.getDegrees()
        // );
        // ((MockCANCoder)drive.getRearRightSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
        //     swerveDriveSimulation.getLatestModulePositions()[3].angle.getDegrees()
        // );

        // update gyro reading from sim
        ((MockGyro) pose.imu).setYaw(this.swerveDriveSimulation.getOdometryEstimatedPose().getRotation().getDegrees());
        // if we want to give the gyro ground truth to make debugging other problems easier swap to this:
        // ((MockGyro) pose.imu).setYaw(this.swerveDriveSimulation.getActualPoseInSimulationWorld().getRotation().getDegrees());


    }
}
