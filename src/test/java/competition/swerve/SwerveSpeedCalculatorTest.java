package competition.swerve;

import competition.BaseCompetitionTest;
import competition.subsystems.drive.DriveSubsystem;
import org.junit.Test;
import xbot.common.controls.actuators.mock_adapters.MockCANSparkMax;
import xbot.common.subsystems.drive.SwerveSpeedCalculator;

import static org.junit.Assert.assertEquals;

public class SwerveSpeedCalculatorTest extends BaseCompetitionTest {

    SwerveSpeedCalculator calculator;

    @Override
    public void setUp() {
        super.setUp();
        calculator = getInjectorComponent().swerveSpeedCalculator();
    }

    @Test
    public void testCalculateTime() {
        assertEquals(SwerveSpeedCalculator.calculateTime(1,0,0,6), Math.sqrt(12), 0.0001);
        assertEquals(SwerveSpeedCalculator.calculateTime(2,0,6,12), Math.sqrt(6), 0.0001);
    }

    @Test
    public void testGetPositionAtPercentage() {
        calculator.setInitialState(0,10,0,0,2);
        calculator.calibrate();
        assertEquals(calculator.getVelocityAtPercentage(25), 1.118, 0.001);
    }
}
