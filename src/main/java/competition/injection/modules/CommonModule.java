package competition.injection.modules;

import javax.inject.Singleton;

import competition.electrical_contract.ElectricalContract;
import competition.simulation.BaseSimulator;
import competition.simulation.NoopSimulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.wpilibj.Preferences;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorDriveSupport;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorPoseSupport;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.GameField;

@Module(subcomponents = { SwerveComponent.class })
public abstract class CommonModule {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(CommonModule.class);

    @Provides
    @Singleton
    public static @FrontLeftDrive SwerveComponent frontLeftSwerveComponent(SwerveComponent.Builder builder) {
        return builder
                .swerveInstance(new SwerveInstance("FrontLeftDrive"))
                .build();
    }

    @Provides
    @Singleton
    public static @FrontRightDrive SwerveComponent frontRightSwerveComponent(SwerveComponent.Builder builder) {
        return builder
                .swerveInstance(new SwerveInstance("FrontRightDrive"))
                .build();
    }

    @Provides
    @Singleton
    public static @RearLeftDrive SwerveComponent rearLeftSwerveComponent(SwerveComponent.Builder builder) {
        return builder
                .swerveInstance(new SwerveInstance("RearLeftDrive"))
                .build();
    }

    @Provides
    @Singleton
    public static @RearRightDrive SwerveComponent rearRightSwerveComponent(SwerveComponent.Builder builder) {
        return builder
                .swerveInstance(new SwerveInstance("RearRightDrive"))
                .build();
    }

    @Provides
    @Singleton
    public static AprilTagFieldLayout fieldLayout() {
        // Initialize the contract to use if this is a fresh robot. Assume competition since that's the safest.
        if (!Preferences.containsKey("AprilTagFieldLayout")) {
            Preferences.setString("AprilTagFieldLayout", "2026_welded");
        }

        String chosenField = Preferences.getString("FieldLayout", "2026_welded");
        switch (chosenField) {
            case "2026_andymark":
                log.info("Using 2026 Andymark April Tag Field Layout.");
                return AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);
            default:
                log.info("Using 2026 Welded April Tag Field Layout default for competition.");
                return AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);
        }
    }

    @Provides
    @Singleton
    public static GameField.Symmetry fieldSymmetry() {
        return GameField.Symmetry.Rotational;
    }

    @Binds
    @Singleton
    public abstract ISwerveAdvisorDriveSupport getSwerveAdvisorDriveSupport(DriveSubsystem impl);

    @Binds
    @Singleton
    public abstract ISwerveAdvisorPoseSupport getSwerveAdvisorPoseSupport(PoseSubsystem impl);

    @Binds
    @Singleton
    public abstract XSwerveDriveElectricalContract getSwerveContract(ElectricalContract impl);

    @Binds
    @Singleton
    public abstract BasePoseSubsystem getPoseSubsystem(PoseSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseSwerveDriveSubsystem getSwerveDriveSubsystem(DriveSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseDriveSubsystem getDriveSubsystem(BaseSwerveDriveSubsystem impl);

    @Binds
    @Singleton
    public abstract BaseSimulator getSimulator(NoopSimulator impl);
}