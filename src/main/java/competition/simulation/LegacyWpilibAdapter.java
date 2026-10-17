package competition.simulation;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveModulePosition;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.units.Units;
import org.wpilib.units.measure.Angle;

/** Copies pose, drivetrain, and heading values between WPILib 2026 and WPILib 2027 classes. */
final class LegacyWpilibAdapter {
    private LegacyWpilibAdapter() {
    }

    static edu.wpi.first.math.geometry.Translation2d toLegacyTranslation(Translation2d translation) {
        return new edu.wpi.first.math.geometry.Translation2d(translation.getX(), translation.getY());
    }

    static edu.wpi.first.math.geometry.Pose2d toLegacyPose(Pose2d pose) {
        return new edu.wpi.first.math.geometry.Pose2d(pose.getX(), pose.getY(),
                new edu.wpi.first.math.geometry.Rotation2d(pose.getRotation().getRadians()));
    }

    static Pose2d fromLegacyPose(edu.wpi.first.math.geometry.Pose2d pose) {
        return new Pose2d(pose.getX(), pose.getY(), new Rotation2d(pose.getRotation().getRadians()));
    }

    static edu.wpi.first.math.kinematics.SwerveModuleState toLegacyState(SwerveModuleVelocity state) {
        return new edu.wpi.first.math.kinematics.SwerveModuleState(state.velocity,
                new edu.wpi.first.math.geometry.Rotation2d(state.angle.getRadians()));
    }

    static SwerveModulePosition fromLegacyPosition(edu.wpi.first.math.kinematics.SwerveModulePosition position) {
        return new SwerveModulePosition(position.distanceMeters, new Rotation2d(position.angle.getRadians()));
    }

    static ChassisVelocities fromLegacyVelocities(edu.wpi.first.math.kinematics.ChassisSpeeds velocity) {
        return new ChassisVelocities(
                velocity.vxMetersPerSecond, velocity.vyMetersPerSecond, velocity.omegaRadiansPerSecond);
    }

    static Angle fromLegacyHeading(edu.wpi.first.math.geometry.Rotation2d rotation) {
        return Units.Radians.of(rotation.getRadians());
    }
}
