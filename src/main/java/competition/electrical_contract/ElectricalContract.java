package competition.electrical_contract;

import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;

public abstract class ElectricalContract {
    public abstract DeviceInfo getLeftLeader();
    public abstract DeviceInfo getRightLeader();
}
