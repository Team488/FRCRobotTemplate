package competition.injection.modules;

import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorDriveSupport;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorPoseSupport;
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
    public static GameField.Symmetry fieldSymmetry() {
        return GameField.Symmetry.Rotational;
    }

    @Binds
    @Singleton
    public abstract ISwerveAdvisorDriveSupport getSwerveAdvisorDriveSupport(DriveSubsystem impl);

    @Binds
    @Singleton
    public abstract ISwerveAdvisorPoseSupport getSwerveAdvisorPoseSupport(PoseSubsystem impl);
}