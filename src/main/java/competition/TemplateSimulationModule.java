package competition;

import competition.subsystems.pose.PoseSubsystem;
import xbot.common.injection.SimulatorModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;


public class TemplateSimulationModule extends SimulatorModule {

    boolean isPractice;

    public TemplateSimulationModule() {
        this.isPractice = true;
    }
    
    @Override
    protected void configure() {
        super.configure();
        this.bind(BasePoseSubsystem.class).to(PoseSubsystem.class);
    }
}
