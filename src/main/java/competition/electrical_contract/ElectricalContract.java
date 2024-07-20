package competition.electrical_contract;

import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;

public abstract class ElectricalContract implements XSwerveDriveElectricalContract {
    public abstract DeviceInfo getLeftLeader();
    public abstract DeviceInfo getRightLeader();
}
