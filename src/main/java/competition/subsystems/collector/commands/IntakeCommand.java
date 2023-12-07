package competition.subsystems.collector.commands;

import competition.subsystems.collector.CollectorMotor;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;


public class IntakeCommand extends BaseCommand {
    CollectorMotor collectorMotor;

    @Inject
    public IntakeCommand(CollectorMotor collectorMotor) {
        this.collectorMotor = collectorMotor;
        addRequirements(collectorMotor);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        collectorMotor.intake();
    }

}
