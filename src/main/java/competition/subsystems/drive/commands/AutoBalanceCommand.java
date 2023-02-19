package competition.subsystems.drive.commands;

import javax.inject.Inject;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import xbot.common.command.BaseCommand;
import xbot.common.controls.sensors.XTimer;
import xbot.common.math.PIDManager;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

public class AutoBalanceCommand extends BaseCommand {
    
    private final DriveSubsystem drive;
    private final PoseSubsystem pose;
    private final PIDManager pidManager;

    private boolean drivingAgainstPositiveAngle = true;
    final double firstAttemptSpeed = 0.10;
    double currentAttemptSpeed = firstAttemptSpeed;
    double lastDetectedFallTime = -1000;

    public enum BalanceState {
        Analyzing,
        Driving,
        FallDetected,
        Waiting,
        Complete
    }

    private final StringProperty balanceStateProp;

    private BalanceState currentBalanceState = BalanceState.Analyzing;

    @Inject
    public AutoBalanceCommand(DriveSubsystem drive, PoseSubsystem pose, PIDManagerFactory pidFactory,
                              PropertyFactory pf) {
        this.drive = drive;
        this.pose = pose;
        this.pidManager = pidFactory.create(this.getPrefix(), 0.1, 0, 0);
        pf.setPrefix(this);

        balanceStateProp = pf.createEphemeralProperty("BalanceState", "Unknown");
    }

    // Basic idea:
    // - Assume that we start at either 15 or -15 degrees
    // - Drive the correct direction at constant velocity
    // - Once we notice a significant change in angle (15->12 or -15->-12), lock the wheels for a bit
    // - After a short time (~1 second?) check the angle and drive in the new correct direction at constant,
    //   but slower, velocity.
    //   Repeat until our resting angle after wheel lock is +/- 1.5 degrees.

    @Override
    public void initialize() {
        log.info("Initializing");
        pidManager.reset();

        drivingAgainstPositiveAngle = getAngle() > 0.0;
        currentAttemptSpeed = firstAttemptSpeed;
        currentBalanceState = BalanceState.Analyzing;
    }

    @Override
    public void execute() {
        // Let's assume that we are already strongly "on the ramp", which is to say, at or very close
        // to the resting angle of the robot, which is +/-15 degrees.

        double currentAngle = getAngle();

        double velocityGoal = 0;
        switch (currentBalanceState) {
            case Analyzing:
                drivingAgainstPositiveAngle = currentAngle > 0.0;
                currentBalanceState = BalanceState.Driving;
                break;
            case Driving:

                // If we are driving against the positive angle, we need to use positive velocity to
                // climb the ramp. If we are driving against negative angle, we need negative velocity
                // to climb the ramp.

                if (drivingAgainstPositiveAngle) {
                    velocityGoal = currentAttemptSpeed;
                } else {
                    velocityGoal = -currentAttemptSpeed;
                }

                // If we're driving against the positive angle, see a sudden drop in angle, we need
                // to advance to FallDetected. Likewise, if we're driving against the negative angle,
                // and we see a sudden increase in angle, we need to advance to FallDetected.

                if (drivingAgainstPositiveAngle && currentAngle < 9) {
                    currentBalanceState = BalanceState.FallDetected;
                } else if (!drivingAgainstPositiveAngle && currentAngle > -9) {
                    currentBalanceState = BalanceState.FallDetected;
                }

                break;
            case FallDetected:
                // We've detected a fall, so we need to stop driving and wait for a bit.
                // We'll also slow down the drive speed for the next attempt.
                currentAttemptSpeed = currentAttemptSpeed * 0.5;
                currentBalanceState = BalanceState.Waiting;
                lastDetectedFallTime = XTimer.getFPGATimestamp();
                velocityGoal = 0;
                break;
            case Waiting:
                if (XTimer.getFPGATimestamp() - lastDetectedFallTime > 1.0) {
                    if (Math.abs(currentAngle) < 1.5) {
                        // We've successfully balanced!
                        currentBalanceState = BalanceState.Complete;
                    } else {
                        // We're still not balanced, so we need to try again.
                        currentBalanceState = BalanceState.Analyzing;
                    }
                }
                velocityGoal = 0;
                break;
            case Complete:
                velocityGoal = 0;
                return;
        }

        balanceStateProp.set(currentBalanceState.toString());
        drive.setVelocityMaintainerXTarget(velocityGoal);
    }

    private double getAngle() {
        // this is based on the rio orientation
        double currentAngle = -(pose.getRobotPitch() - 1.75);


        if (Math.abs(currentAngle) < 1.5) {
            currentAngle = 0;
        }

        return currentAngle;
    }

    @Override
    public boolean isFinished() {
        return currentBalanceState == BalanceState.Complete;
    }

    @Override
    public void end(boolean interrupted) {
        drive.setVelocityMaintainerXTarget(0);
    }
}
