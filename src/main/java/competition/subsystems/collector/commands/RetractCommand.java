package competition.subsystems.collector.commands;

import javax.inject.Inject;
import competition.subsystems.collector.CollectorPiston;
import xbot.common.command.BaseCommand;

public class RetractCommand extends BaseCommand{
    CollectorPiston collectorPiston;

    @Inject
    public RetractCommand(CollectorPiston collectorPiston) {
        this.collectorPiston = collectorPiston;
        addRequirements(collectorPiston);
    }

    @Override
    public void initialize() {
        collectorPiston.retract();
    }

    @Override
    public void execute() {

    }

    public boolean isFinished() {
        return true;
    }
}