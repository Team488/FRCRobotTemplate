package competition.subsystems.drive;

import javax.inject.Inject;
import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

@Singleton
public class DriveSubsystem extends BaseSwerveDriveSubsystem implements DataFrameRefreshable {
    private static Logger log = LogManager.getLogger(DriveSubsystem.class);

    @Inject
    public DriveSubsystem(XCANSparkMax.XCANSparkMaxFactory sparkMaxFactory, XPropertyManager propManager,
                          ElectricalContract contract, PIDManagerFactory pidFactory, PropertyFactory pf,
                          @FrontLeftDrive SwerveComponent frontLeftSwerve, @FrontRightDrive SwerveComponent frontRightSwerve,
                          @RearLeftDrive SwerveComponent rearLeftSwerve, @RearRightDrive SwerveComponent rearRightSwerve) {

        super(pidFactory, pf, frontLeftSwerve, frontRightSwerve, rearLeftSwerve, rearRightSwerve);
        log.info("Creating DriveSubsystem");

        pf.setPrefix(this.getPrefix());
        pf.setDefaultLevel(Property.PropertyLevel.Important);
    }
}
