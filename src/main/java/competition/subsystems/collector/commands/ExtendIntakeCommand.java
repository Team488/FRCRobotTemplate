package competition.subsystems.collector.commands;

import competition.subsystems.collector.CollectorMotor;
import competition.subsystems.collector.CollectorPiston;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;
public class ExtendIntakeCommand extends BaseCommand {

    CollectorMotor collectorMotor;
    CollectorPiston collectorPiston;

    @Inject
    public ExtendIntakeCommand(CollectorPiston collectorPiston, CollectorMotor collectorMotor){
        this.collectorMotor = collectorMotor;
        this.collectorPiston = collectorPiston;
    }

    @Override
    public void initialize() {
        collectorPiston.extend();
        collectorMotor.setTime(5);
    }

    @Override
    public void execute() {
        collectorMotor.intakeSeconds();
    }

    public boolean isFinished() {
        if (collectorMotor.getIntakeState() == CollectorMotor.IntakeState.stopped) {
            collectorPiston.retract();
            return true;
        }
        return false;

    }


}

