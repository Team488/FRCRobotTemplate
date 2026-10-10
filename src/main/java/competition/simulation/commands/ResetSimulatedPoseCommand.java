package competition.simulation.commands;

import javax.inject.Inject;

import competition.simulation.BaseSimulator;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import xbot.common.command.BaseCommand;

public class ResetSimulatedPoseCommand extends BaseCommand {
    BaseSimulator simulator;

    @Inject
    public ResetSimulatedPoseCommand(BaseSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void initialize() {
        this.simulator.resetPosition(new Pose2d(6, 4, new Rotation2d()));
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}