package site.siredvin.lib.peripherals;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import site.siredvin.lib.peripherals.owner.IPeripheralOwner;

import java.util.List;

public interface IBasePeripheral<T extends IPeripheralOwner> extends IPeripheral {
    boolean isEnabled();
    List<IComputerAccess> getConnectedComputers();
    T getPeripheralOwner();
}
