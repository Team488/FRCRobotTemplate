package competition.simulation;

import javax.inject.Inject;
import javax.inject.Singleton;

import competition.Robot;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import org.wpilib.driverstation.RobotState;
import org.wpilib.math.geometry.Pose2d;
import xbot.common.advantage.AKitLogger;

/** Moves the robot pose from commanded wheel speeds; no physics or sensor simulation. */
@Singleton
public class BasicDrivetrainSimulator implements BaseSimulator {
    private final PoseSubsystem pose;
    private final DriveSubsystem drive;
    private final AKitLogger log = new AKitLogger("Simulator/");

    @Inject
    public BasicDrivetrainSimulator(PoseSubsystem pose, DriveSubsystem drive) {
        this.pose = pose;
        this.drive = drive;
    }

    @Override
    public void update() {
        if (RobotState.isEnabled()) {
            var velocity = drive.getSwerveDriveKinematics()
                    .toChassisVelocities(drive.getTargetSwerveStates().toArray());
            // Move in the robot's frame for one loop, then write the new field pose.
            resetPosition(pose.getCurrentPose2d().plus(velocity.toTwist2d(Robot.LOOP_INTERVAL).exp()));
        }
        log.record("FieldSimulation/Robot", getGroundTruthPose());
    }

    @Override
    public void resetPosition(Pose2d newPose) {
        pose.setCurrentPoseInMeters(newPose);
    }

    @Override
    public Pose2d getGroundTruthPose() {
        return pose.getCurrentPose2d();
    }
}
