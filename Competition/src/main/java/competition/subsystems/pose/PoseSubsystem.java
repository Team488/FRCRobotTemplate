package competition.subsystems.pose;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@Singleton
public class PoseSubsystem extends BasePoseSubsystem {

    @Inject
    public PoseSubsystem(CommonLibFactory clf, PropertyFactory propManager) {
        super(clf, propManager);
    }


    @Override
    protected double getLeftDriveDistance() {
        return 0;
    }

    @Override
    protected double getRightDriveDistance() {
        return 0;
    }

}