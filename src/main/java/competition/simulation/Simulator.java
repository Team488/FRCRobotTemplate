package competition.simulation;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.SelfControlledSwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;


public class Simulator {
    PoseSubsystem pose;
    DriveSubsystem drive;
    final double robotTopSpeedInMetersPerSecond = 3.0;
    final double robotLoopPeriod = 0.02;
    final double robotTopAngularSpeedInDegreesPerSecond = 360;
    final double poseAdjustmentFactorForDriveSimulation = robotTopSpeedInMetersPerSecond * robotLoopPeriod;
    final double headingAdjustmentFactorForDriveSimulation = robotTopAngularSpeedInDegreesPerSecond * robotLoopPeriod;

    // maple-sim stuff
    // TODO: update config with real values
    final DriveTrainSimulationConfig config = DriveTrainSimulationConfig.Default();
    private final Field2d field2d;
    final SimulatedArena arena;
    final SelfControlledSwerveDriveSimulation swerveDriveSimulation;

    @Inject
    public Simulator(PoseSubsystem pose, DriveSubsystem drive) {
        this.pose = pose;
        this.drive = drive;

        arena = SimulatedArena.getInstance();

        config.withCustomModuleTranslations(new Translation2d[]{
            drive.getFrontLeftSwerveModuleSubsystem().getModuleTranslation(),
            drive.getFrontRightSwerveModuleSubsystem().getModuleTranslation(),
            drive.getRearLeftSwerveModuleSubsystem().getModuleTranslation(),
            drive.getRearRightSwerveModuleSubsystem().getModuleTranslation()
        });
        // Creating the SelfControlledSwerveDriveSimulation instance
        this.swerveDriveSimulation = new SelfControlledSwerveDriveSimulation(
                new SwerveDriveSimulation(config, new Pose2d(0, 0, new Rotation2d())));

        arena.addDriveTrainSimulation(swerveDriveSimulation.getDriveTrainSimulation());

        field2d = new Field2d();
        SmartDashboard.putData("simulation field", field2d);
    }

    public void update() {
        

       
        // drive simulated motors from requested robot commands
        swerveDriveSimulation.runSwerveStates(new SwerveModuleState[]{
            drive.getFrontLeftSwerveModuleSubsystem().getTargetState(),
            drive.getFrontRightSwerveModuleSubsystem().getTargetState(),
            drive.getRearLeftSwerveModuleSubsystem().getTargetState(),
            drive.getRearRightSwerveModuleSubsystem().getTargetState()
        });

        arena.simulationPeriodic();
        swerveDriveSimulation.periodic();

        // read values back out from sim
        field2d.setRobotPose(swerveDriveSimulation.getActualPoseInSimulationWorld());
        field2d.getObject("odometry").setPose(swerveDriveSimulation.getOdometryEstimatedPose());

         // update gyro reading from sim
         pose.setCurrentHeading(this.swerveDriveSimulation.getOdometryEstimatedPose().getRotation().getDegrees());

         // update position from sim
         pose.setCurrentPosition(this.swerveDriveSimulation.getOdometryEstimatedPose().getTranslation().getX(),
                 this.swerveDriveSimulation.getOdometryEstimatedPose().getTranslation().getY());
 
 
        
    }
}
