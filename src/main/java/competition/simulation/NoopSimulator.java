package competition.simulation;

import javax.inject.Inject;

public class NoopSimulator implements BaseSimulator {
    @Inject
    public NoopSimulator() {
        // Do nothing
    }

    public void update() {
        // Do nothing, used just in case a real robot accidentally tries to call this method
    }
}
