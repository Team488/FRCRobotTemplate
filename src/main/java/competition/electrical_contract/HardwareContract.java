package competition.electrical_contract;

import java.util.Set;

/**
 * Any year-specific Hardware getters goes here.
 */
public abstract class HardwareContract extends ElectricalContract {
    protected HardwareContract(Set<Hardware> readinessSet) {
        super(readinessSet);
    }
}
