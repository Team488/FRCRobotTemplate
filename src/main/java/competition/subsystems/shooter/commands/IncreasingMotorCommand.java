package competition.subsystems.shooter.commands;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.shooter.ShooterSubsystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;

public class IncreasingMotorCommand extends BaseCommand {
    private static Logger log = LogManager.getLogger(DriveSubsystem.class);
    ShooterSubsystem shooter;


    @Inject
    public IncreasingMotorCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        this.addRequirements(shooter);
    }


    @Override
    public void initialize() {
        shooter.increaseMotorRpm();
        log.info("Increasing RPM");
    }

}