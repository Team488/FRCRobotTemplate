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
public class Simulator {
    PoseSubsystem pose;
    DriveSubsystem drive;
    final double robotTopSpeedInMetersPerSecond = 3.0;
    final double robotLoopPeriod = 0.02;
    final double robotTopAngularSpeedInDegreesPerSecond = 360;
    final double poseAdjustmentFactorForDriveSimulation = robotTopSpeedInMetersPerSecond * robotLoopPeriod;
    final double headingAdjustmentFactorForDriveSimulation = robotTopAngularSpeedInDegreesPerSecond * robotLoopPeriod;

    // maple-sim stuff
    final DriveTrainSimulationConfig config;
    private final Field2d field2d;
    final SimulatedArena arena;
    final SelfControlledSwerveDriveSimulation swerveDriveSimulation;

    @Inject
    public Simulator(PoseSubsystem pose, DriveSubsystem drive) {
        this.pose = pose;
        this.drive = drive;

        arena = SimulatedArena.getInstance();
        // more custom things to provide here like motor ratios and what have you
        config = DriveTrainSimulationConfig.Default().withCustomModuleTranslations(new Translation2d[] {
                drive.getFrontLeftSwerveModuleSubsystem().getModuleTranslation(),
                drive.getFrontRightSwerveModuleSubsystem().getModuleTranslation(),
                drive.getRearLeftSwerveModuleSubsystem().getModuleTranslation(),
                drive.getRearRightSwerveModuleSubsystem().getModuleTranslation()
        });
        // Creating the SelfControlledSwerveDriveSimulation instance
        this.swerveDriveSimulation = new SelfControlledSwerveDriveSimulation(
                new SwerveDriveSimulation(config, new Pose2d(6, 4, new Rotation2d())));

        arena.addDriveTrainSimulation(swerveDriveSimulation.getDriveTrainSimulation());

        field2d = new Field2d();
        SmartDashboard.putData("simulation field", field2d);
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
        field2d.setRobotPose(swerveDriveSimulation.getActualPoseInSimulationWorld());
        field2d.getObject("odometry").setPose(swerveDriveSimulation.getOdometryEstimatedPose());

        ((MockCANMotorController)drive.getFrontLeftSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
            Rotations.of(swerveDriveSimulation.getLatestModulePositions()[0].distanceMeters)
        );
        ((MockCANMotorController)drive.getFrontRightSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
            Rotations.of(swerveDriveSimulation.getLatestModulePositions()[1].distanceMeters)
        );
        ((MockCANMotorController)drive.getRearLeftSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
            Rotations.of(swerveDriveSimulation.getLatestModulePositions()[2].distanceMeters)
        );
        ((MockCANMotorController)drive.getRearRightSwerveModuleSubsystem().getDriveSubsystem().motorController).setPosition(
            Rotations.of(swerveDriveSimulation.getLatestModulePositions()[3].distanceMeters)
        );

        ((MockCANCoder)drive.getFrontLeftSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
            swerveDriveSimulation.getLatestModulePositions()[0].angle.getDegrees()
        );
        ((MockCANCoder)drive.getFrontRightSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
            swerveDriveSimulation.getLatestModulePositions()[1].angle.getDegrees()
        );
        ((MockCANCoder)drive.getRearLeftSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
            swerveDriveSimulation.getLatestModulePositions()[2].angle.getDegrees()
        );
        ((MockCANCoder)drive.getRearRightSwerveModuleSubsystem().getSteeringSubsystem().encoder).setAbsolutePosition(
            swerveDriveSimulation.getLatestModulePositions()[3].angle.getDegrees()
        );

        // update gyro reading from sim
        //
        ((MockGyro) pose.imu).setYaw(this.swerveDriveSimulation.getOdometryEstimatedPose().getRotation().getDegrees());
        // lets give the gyro ground truth to make debugging other problems easier
        // ((MockGyro) pose.imu).setYaw(this.swerveDriveSimulation.getActualPoseInSimulationWorld().getRotation().getDegrees());

        // update position from sim
        // TODO: debug why this isn't working, don't see the robot position from the
        // pose subsystem updating in network tables
        pose.setCurrentPosition(this.swerveDriveSimulation.getOdometryEstimatedPose().getTranslation().getX(),
                this.swerveDriveSimulation.getOdometryEstimatedPose().getTranslation().getY());

    }
}
