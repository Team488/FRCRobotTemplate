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

        // TODO: for testing, remake this so that XbotSwervePoints are set up properly again
        SwervePointKinematics kinematicValuesForTesting = new SwervePointKinematics(0.2, 0, 2, 5);

        var s1 = swerveSimpleTrajectoryCommandProvider.get();
        List<XbotSwervePoint> points = new ArrayList<>();
        XbotSwervePoint p1 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(2, 2), new Rotation2d(), 10);
        p1.setKinematics(kinematicValuesForTesting);
        points.add(p1);
        s1.logic.setKeyPoints(points);
        s1.logic.setVelocityMode(SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints);

        var s2 = swerveSimpleTrajectoryCommandProvider.get();
        List<XbotSwervePoint> points2 = new ArrayList<>();
        XbotSwervePoint p2 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(0, 0), new Rotation2d(), 10);
        p2.setKinematics(kinematicValuesForTesting);
        points2.add(p2);
        s2.logic.setKeyPoints(points2);
        s2.logic.setVelocityMode(SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints);

        var s3 = swerveSimpleTrajectoryCommandProvider.get();
        List<XbotSwervePoint> points3 = new ArrayList<>();
        XbotSwervePoint p3 = XbotSwervePoint.createPotentiallyFilppedXbotSwervePoint(new Translation2d(1, 4), new Rotation2d(), 10);
        p3.setKinematics(kinematicValuesForTesting);
        points3.add(p3);
        s3.logic.setKeyPoints(points3);
        s3.logic.setVelocityMode(SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints);

        operatorInterface.gamepad.getifAvailable(XXboxController.XboxButton.X).onTrue(s1);
        operatorInterface.gamepad.getifAvailable(XXboxController.XboxButton.B).onTrue(s2);
        operatorInterface.gamepad.getifAvailable(XXboxController.XboxButton.Y).onTrue(s3);
    }
}
