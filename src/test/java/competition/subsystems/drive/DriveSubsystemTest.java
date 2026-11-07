package competition.subsystems.drive;

import org.junit.jupiter.api.Test;

import competition.BaseCompetitionTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DriveSubsystemTest extends BaseCompetitionTest {
    @Test
    public void testDriveSubsystem() {
        DriveSubsystem driveSubsystem = (DriveSubsystem) getInjectorComponent().driveSubsystem();
        assertNotEquals(driveSubsystem, null);
    }
}
