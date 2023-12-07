package competition.subsystems.collector.commands;

import competition.subsystems.collector.CollectorMotor;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;


public class StopCommand extends BaseCommand {
    CollectorMotor collectorMotor;

    @Inject
    public StopCommand(CollectorMotor collectorMotor) {
        this.collectorMotor = collectorMotor;
        addRequirements(collectorMotor);
    }

    @Override
    public void initialize() {
        collectorMotor.stop();
    }

    @Override
    public void execute() {

    }

    public boolean isFinished() {
        return true;
    }
}
