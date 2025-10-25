package competition.subsystems.shooter.commands;

import competition.subsystems.shooter.ShooterSubsystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;

public class DecreasingMotorCommand extends BaseCommand {
    private static Logger log = LogManager.getLogger(DecreasingMotorCommand.class);
    ShooterSubsystem shooter;


    @Inject
    public DecreasingMotorCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        this.addRequirements(shooter);
    }


    @Override
    public void initialize() {
        shooter.decreaseMotorRpm();
        log.info("Decreasing");
    }

}