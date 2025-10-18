package competition.subsystems.shooter.commands;

import competition.subsystems.shooter.ShooterSubsystem;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;

public class StartMotorCommand extends BaseCommand {
    ShooterSubsystem shooter;


    @Inject
    public StartMotorCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        this.addRequirements(shooter);
    }


    @Override
    public void initialize() {
        shooter.startMotor();

    }

}