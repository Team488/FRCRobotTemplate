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
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

@Singleton
public class DriveSubsystem extends BaseSwerveDriveSubsystem implements DataFrameRefreshable {
    private static Logger log = LogManager.getLogger(DriveSubsystem.class);
    
    ElectricalContract contract;

    public final XCANSparkMax leftLeader;
    public final XCANSparkMax rightLeader;

    private final PIDManager positionPid;
    private final PIDManager rotationPid;

    private double scalingFactorFromTicksToInches = 1.0 / 256.0;

    @Inject
    public DriveSubsystem(XCANSparkMax.XCANSparkMaxFactory sparkMaxFactory, XPropertyManager propManager,
                          ElectricalContract contract, PIDManagerFactory pidFactory, PropertyFactory pf,
                          @FrontLeftDrive SwerveComponent frontLeftSwerve, @FrontRightDrive SwerveComponent frontRightSwerve,
                          @RearLeftDrive SwerveComponent rearLeftSwerve, @RearRightDrive SwerveComponent rearRightSwerve) {

        super(pidFactory, pf, frontLeftSwerve, frontRightSwerve, rearLeftSwerve, rearRightSwerve);
        log.info("Creating DriveSubsystem");

        pf.setPrefix(this);

        this.leftLeader = sparkMaxFactory.create(contract.getLeftLeader(), this.getPrefix(), "LeftLeader");
        this.rightLeader = sparkMaxFactory.create(contract.getRightLeader(), this.getPrefix(), "RightLeader");

        positionPid = pidFactory.create(getPrefix() + "PositionPID");
        rotationPid = pidFactory.create(getPrefix() + "RotationPID");
    }

    public void tankDrive(double leftPower, double rightPower) {
        this.leftLeader.set(leftPower);
        this.rightLeader.set(rightPower);
    }

    @Override
    public PIDManager getPositionalPid() {
        return positionPid;
    }

    @Override
    public PIDManager getRotateToHeadingPid() {
        return rotationPid;
    }

    @Override
    public PIDManager getRotateDecayPid() {
        return null;
    }

    @Override
    public void move(XYPair translate, double rotate) {
        double y = translate.y;

        double left = y - rotate;
        double right = y + rotate;

        this.leftLeader.set(left);
        this.rightLeader.set(right);
    }

    @Override
    public double getLeftTotalDistance() {
        return leftLeader.getPosition() * scalingFactorFromTicksToInches;
    }

    @Override
    public double getRightTotalDistance() {
        return rightLeader.getPosition() * scalingFactorFromTicksToInches;
    }

    @Override
    public double getTransverseDistance() {
        return 0;
    }

    @Override
    public void refreshDataFrame() {
        leftLeader.refreshDataFrame();
        rightLeader.refreshDataFrame();
    }
}
