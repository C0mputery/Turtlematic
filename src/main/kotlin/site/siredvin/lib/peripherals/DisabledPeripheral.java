package site.siredvin.lib.peripherals;

import dan200.computercraft.api.pocket.IPocketAccess;
import site.siredvin.lib.peripherals.owner.PocketPeripheralOwner;

public class DisabledPeripheral extends BasePeripheral<PocketPeripheralOwner> {
    public final static DisabledPeripheral INSTANCE = new DisabledPeripheral("disabledPeripheral", null);

    private DisabledPeripheral(String type, IPocketAccess access) {
        super(type, new PocketPeripheralOwner(access));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
