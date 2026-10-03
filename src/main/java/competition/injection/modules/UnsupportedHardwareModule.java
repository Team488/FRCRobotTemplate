package competition.injection.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xbot.common.controls.sensors.XLaserCAN.XLaserCANFactory;

/** Hardware whose real adapter is unavailable in SCL's 2027 alpha branch. */
@Module
public final class UnsupportedHardwareModule {
    private UnsupportedHardwareModule() {
    }

    @Provides
    @Singleton
    public static XLaserCANFactory laserCANFactory() {
        return (info, prefix) -> {
            throw new UnsupportedOperationException("LaserCAN hardware is unavailable in the WPILib 2027 alpha migration");
        };
    }
}
