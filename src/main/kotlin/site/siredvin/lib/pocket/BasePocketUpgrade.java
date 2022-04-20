package site.siredvin.lib.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import site.siredvin.lib.peripherals.DisabledPeripheral;
import site.siredvin.lib.peripherals.IBasePeripheral;

import java.util.function.Supplier;

public abstract class BasePocketUpgrade<T extends IBasePeripheral<?>> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocketUpgrade(ResourceLocation id, String adjective, Supplier<? extends ItemLike> stack) {
        super(id, adjective, stack);
    }

    protected BasePocketUpgrade(ResourceLocation id, Supplier<? extends ItemLike> stack) {
        super(id, "hahah1", stack);
//        super(id, TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        peripheral = getPeripheral(access);
        if (!peripheral.isEnabled())
            return DisabledPeripheral.INSTANCE;
        return peripheral;
    }
}
