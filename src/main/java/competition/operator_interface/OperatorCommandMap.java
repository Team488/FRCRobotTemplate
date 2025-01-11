package competition.operator_interface;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryCommand;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryMode;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;
import xbot.common.trajectory.XbotSwervePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps operator interface buttons to commands
 */
@Singleton
public class OperatorCommandMap {

    @Inject
    public OperatorCommandMap() {}
    
    // Example for setting up a command to fire when a button is pressed:
    @Inject
    public void setupMyCommands(
            OperatorInterface operatorInterface,
            SetRobotHeadingCommand resetHeading,
            Provider<SwerveSimpleTrajectoryCommand> swerveSimpleTrajectoryCommandProvider) {
        resetHeading.setHeadingToApply(0);
        operatorInterface.gamepad.getifAvailable(1).onTrue(resetHeading);

        // Below are for testing purposes only!!!
        SwervePointKinematics kinematicValuesForTesting = new SwervePointKinematics(1, 0, 0, 1000);

        var s1 = swerveSimpleTrajectoryCommandProvider.get();
        List<XbotSwervePoint> points = new ArrayList<>();
        XbotSwervePoint p1 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(2, 2), new Rotation2d(), 10);
        p1.setKinematics(kinematicValuesForTesting);
        points.add(p1);
        s1.logic.setKeyPoints(points);
        s1.logic.setVelocityMode(SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints);

        var s3 = swerveSimpleTrajectoryCommandProvider.get();
        List<XbotSwervePoint> points3 = new ArrayList<>();
        XbotSwervePoint p3 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(15, 4), new Rotation2d(Math.toDegrees(45)), 10);
        XbotSwervePoint p4 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(1, 5), new Rotation2d(), 10);
        s3.logic.setGlobalKinematicValues(kinematicValuesForTesting);
        points3.add(p3);
        points3.add(p4);
        s3.logic.setKeyPoints(points3);
        s3.logic.setVelocityMode(SwerveSimpleTrajectoryMode.KinematicsForPointsList);

        operatorInterface.gamepad.getifAvailable(2).onTrue(s1);
        operatorInterface.gamepad.getifAvailable(3).onTrue(s3);
    }
}
