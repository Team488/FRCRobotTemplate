package competition.electrical_contract;

import java.util.Set;

public abstract class GeneralContract extends ElectricalContract {
    protected GeneralContract(Set<Hardware> readinessSet) {
        super(readinessSet);
    }
}