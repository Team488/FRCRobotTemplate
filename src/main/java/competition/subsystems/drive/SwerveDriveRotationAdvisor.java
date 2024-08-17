package competition.subsystems.drive;

import competition.subsystems.pose.PoseSubsystem;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;

public class SwerveDriveRotationAdvisor {
    // Suggests rotation input for swerve drive
    // Should this be a singleton? No reason not to...? (for now)

    HumanVsMachineDecider hvmDecider;
    PoseSubsystem pose;
    DriveSubsystem drive;

    public SwerveDriveRotationAdvisor(PoseSubsystem pose, DriveSubsystem drive,
                                      HumanVsMachineDeciderFactory hvmFactory) {
        this.hvmDecider = hvmFactory;
    }
}
