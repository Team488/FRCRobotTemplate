package competition.subsystems.drive;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.pose.PoseSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.advantage.AKitLogger;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class SwerveDriveRotationAdvisor {
    HumanVsMachineDecider hvmDecider;
    PoseSubsystem pose;
    DriveSubsystem drive;

    DoubleProperty minimumMagnitudeToSnap;

    AKitLogger aKitLogger;

    @Inject
    public SwerveDriveRotationAdvisor(PoseSubsystem pose, DriveSubsystem drive, PropertyFactory pf,
                                      HumanVsMachineDeciderFactory hvmFactory, OperatorInterface oi) {
        pf.setPrefix("SwerveDriveRotationAdvisor/");
        this.hvmDecider = hvmFactory.create(pf.getPrefix());
        this.drive = drive;
        this.pose = pose;

        aKitLogger = new AKitLogger(pf.getPrefix());

        this.minimumMagnitudeToSnap = pf.createPersistentProperty("MinimumMagnitudeToSnap", 0.75);
    }

    public SuggestedRotationValue getSuggestedRotationValue(XYPair snappingInput, double triggerRotateIntent) {
        SuggestedRotationValue suggested;

        if (snappingInput.getMagnitude() >= minimumMagnitudeToSnap.get()) {
            suggested = evaluateSnappingInput(snappingInput);
        } else if (drive.getLookAtPointActive()) {
            suggested = evaluateLookAtPoint();
        } else if (drive.getStaticHeadingActive()) {
            suggested = evaluateStaticHeading();
        } else {
            suggested = evaluateLastKnownHeading(triggerRotateIntent);
        }

        return suggested;
    }

    private SuggestedRotationValue evaluateSnappingInput(XYPair input) {
        double heading = input.getAngle();

        // Rebound the heading to be within -45 to 315 (diagnal X) then shift to 0 to 360 (for division purposes)
        double reboundedHeading = ContiguousDouble.reboundValue(heading, -45, 315) + 45;

        // Get which quadrant our rebounded heading is in
        int quadrant = (int) (reboundedHeading / 90);
        double desiredHeading = switch (quadrant) {
            // Modify here if specific heading for certain quadrant(s)
            default -> quadrant * 90;
        };

        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            desiredHeading += 180;
        }

        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        } else {
            drive.setDesiredHeading(desiredHeading);
        }
        hvmDecider.reset();
        return new SuggestedRotationValue(desiredHeading, SuggestedRotationValue.RotationGoalType.DesiredHeading);
    }

    /** Stalk a point in 2d space, does not take in consideration of height */
    private SuggestedRotationValue evaluateLookAtPoint() {
        Translation2d target = drive.getLookAtPointTarget();

        Pose2d currentPose = pose.getCurrentPose2d();
        aKitLogger.record("CurrentPoseX", currentPose.getX());
        aKitLogger.record("CurrentPoseY", currentPose.getY());
        Translation2d currentXY = new Translation2d(currentPose.getX(), currentPose.getY());

        double desiredHeading = currentXY.minus(target).getAngle().getDegrees() + 180;
        drive.setDesiredHeading(desiredHeading);
        return new SuggestedRotationValue(desiredHeading, SuggestedRotationValue.RotationGoalType.DesiredHeading);
    }

    /** Look in a specific angle, statically */
    private SuggestedRotationValue evaluateStaticHeading() {
        double desiredHeading = drive.getStaticHeadingTarget().getDegrees();
        drive.setDesiredHeading(desiredHeading);
        return new SuggestedRotationValue(desiredHeading, SuggestedRotationValue.RotationGoalType.DesiredHeading);
    }

    private SuggestedRotationValue evaluateLastKnownHeading(double triggerRotateIntent) {
        HumanVsMachineMode recommendedMode = hvmDecider.getRecommendedMode(triggerRotateIntent);
        aKitLogger.record("HvmRecommendedMode", recommendedMode);
        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        }

        return switch (recommendedMode) {
            case HumanControl -> {
                if (drive.isPrecisionRotationActive()) {
                    triggerRotateIntent *= 0.25;
                }
                yield new SuggestedRotationValue(
                        triggerRotateIntent,
                        SuggestedRotationValue.RotationGoalType.HeadingPower
                );
            }
            case InitializeMachineControl -> {
                drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
                yield new SuggestedRotationValue();
            }
            case MachineControl -> {
                yield new SuggestedRotationValue(
                        drive.getDesiredHeading(),
                        SuggestedRotationValue.RotationGoalType.DesiredHeading
                );
            }
            case Coast -> {
                yield new SuggestedRotationValue();
            }
        };
    }

    public void resetDecider() {
        hvmDecider.reset();
    }
}