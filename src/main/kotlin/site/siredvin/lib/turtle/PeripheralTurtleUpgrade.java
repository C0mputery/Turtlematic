package site.siredvin.lib.turtle;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import site.siredvin.lib.peripherals.DisabledPeripheral;
import site.siredvin.lib.peripherals.IBasePeripheral;

import java.nio.FloatBuffer;

public abstract class PeripheralTurtleUpgrade<T extends IBasePeripheral<?>> extends AbstractTurtleUpgrade {
    protected int tick;

    public PeripheralTurtleUpgrade(ResourceLocation id, String adjective, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, adjective, item);
    }

    public PeripheralTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, "hahaha1", item);
//        super(id, TurtleUpgradeType.PERIPHERAL, TranslationUtil.turtle(id.getPath()), item);
    }

    protected abstract ModelResourceLocation getLeftModel();

    protected abstract ModelResourceLocation getRightModel();

    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        if (getLeftModel() == null) {
            float xOffset = turtleSide == TurtleSide.LEFT ? -0.40625f : 0.40625f;
            Matrix4f transform = new Matrix4f();
            transform.load(FloatBuffer.wrap(new float[]{
                    0.0f, 0.0f, -1.0f, 1.0f + xOffset,
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, -1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
            }));
            return TransformedModel.of(getCraftingItem(), new Transformation(transform));
        }
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? getLeftModel() : getRightModel());
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        T peripheral = buildPeripheral(turtle, side);
        if (!peripheral.isEnabled()) {
            return DisabledPeripheral.INSTANCE;
        }
        return peripheral;
    }
}