package competition.subsystems.drive.commands;

import com.google.inject.Inject;

import competition.subsystems.drive.DriveSubsystem;
import xbot.common.command.BaseCommand;

public class SpinCommand extends BaseCommand {

    final DriveSubsystem driveSubsystem;

    @Inject
    public SpinCommand(DriveSubsystem driveSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.addRequirements(this.driveSubsystem);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        driveSubsystem.tankDrive(1f, -1f);
    }

    @Override
    public void execute() {
        
    }
}
