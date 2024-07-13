package competition.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import competition.BaseCompetitionTest;

public class DriveSubsystemTest extends BaseCompetitionTest {
    @Test
    public void testTankDrive() {
        DriveSubsystem driveSubsystem = (DriveSubsystem)getInjectorComponent().driveSubsystem();
        driveSubsystem.tankDrive(1, 1);
        driveSubsystem.refreshDataFrame();

        assertEquals(1, driveSubsystem.leftLeader.getAppliedOutput(), 0.001);
        assertEquals(1, driveSubsystem.rightLeader.getAppliedOutput(), 0.001);
    }
}
