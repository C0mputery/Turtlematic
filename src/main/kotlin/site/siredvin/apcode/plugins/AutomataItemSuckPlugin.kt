package site.siredvin.apcode.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import site.siredvin.lib.operations.SingleOperation
import site.siredvin.lib.peripherals.BaseAutomataCorePeripheral
import site.siredvin.lib.peripherals.IPeripheralFunction
import site.siredvin.lib.peripherals.IPeripheralOperation
import site.siredvin.lib.peripherals.owner.TurtlePeripheralOwner
import site.siredvin.lib.util.LuaConverter

class AutomataItemSuckPlugin(automataCore: BaseAutomataCorePeripheral) : AutomataCorePlugin(automataCore) {
    override val operations: Array<IPeripheralOperation<*>>
        get() = arrayOf(SingleOperation.SUCK)

    protected fun getBox(pos: BlockPos): AABB {
        val x: Int = pos.x
        val y: Int = pos.y
        val z: Int = pos.z
        val interactionRadius = automataCore.interactionRadius
        return AABB(
            (x - interactionRadius).toDouble(), (y - interactionRadius).toDouble(), (z - interactionRadius).toDouble(),
            (x + interactionRadius).toDouble(), (y + interactionRadius).toDouble(), (z + interactionRadius).toDouble()
        )
    }

    protected val items: List<ItemEntity>
        get() {
            val owner: TurtlePeripheralOwner = automataCore.peripheralOwner
            return owner.level!!.getEntitiesOfClass(ItemEntity::class.java, getBox(owner.pos))
        }

    protected fun suckItem(entity: ItemEntity, requiredQuantity: Int): Int {
        var requiredQuantityMutable = requiredQuantity
        val stack: ItemStack = entity.item.copy()
        val storeStack: ItemStack
        val leaveStack: ItemStack
        if (stack.count > requiredQuantityMutable) {
            storeStack = stack.split(requiredQuantityMutable)
            leaveStack = stack
        } else {
            storeStack = stack
            leaveStack = ItemStack.EMPTY
        }
        val remainder: ItemStack = automataCore.peripheralOwner.storeItem(storeStack)
        if (remainder != storeStack) {
            if (remainder.isEmpty && leaveStack.isEmpty) {
                entity.remove(Entity.RemovalReason.KILLED)
            } else if (remainder.isEmpty) {
                entity.item = leaveStack
            } else if (leaveStack.isEmpty) {
                entity.item = remainder
            } else {
                leaveStack.grow(remainder.count)
                entity.item = leaveStack
            }
        }
        requiredQuantityMutable -= storeStack.count
        return requiredQuantity
    }

    @LuaFunction(mainThread = true)
    fun scanItems(): MethodResult {
        automataCore.addRotationCycle()
        val items: List<ItemEntity> = items
        val data: MutableMap<Int, Map<String, Any?>> = HashMap()
        var index = 1
        for (item in items) {
            val itemData: MutableMap<String, Any?> = HashMap()
            itemData["entity_id"] = item.id
            itemData["name"] = item.item.displayName.string
            val itemName: ResourceLocation = Registry.ITEM.getKey(item.item.item)
            itemData["technicalName"] = itemName.toString()
            itemData["count"] = item.item.count
            itemData["tags"] = LuaConverter.tagsToList(item.item.item.builtInRegistryHolder().tags())
            data[index] = itemData
            index++
        }
        return MethodResult.of(data)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun collectSpecificItem(arguments: IArguments): MethodResult {
        val technicalName: String = arguments.getString(0)
        val requiredQuantityArg: Int = arguments.optInt(1, Int.MAX_VALUE)
        return automataCore.withOperation(
            SingleOperation.SUCK
        ) {
            val items: List<ItemEntity> = items
            var requiredQuantity = requiredQuantityArg
            for (item in items) {
                val itemName: ResourceLocation = Registry.ITEM.getKey(item.item.item) ?: continue
                if (itemName.toString() == technicalName) {
                    requiredQuantity -= suckItem(item, requiredQuantity)
                }
                if (requiredQuantity <= 0) break
            }
            MethodResult.of(true)
        }
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun collectItems(arguments: IArguments): MethodResult {
        val requiredQuantityArg: Int = arguments.optInt(0, Int.MAX_VALUE)
        return automataCore.withOperation(
            SingleOperation.SUCK,
            IPeripheralFunction {
                if (requiredQuantityArg == 0) {
                    return@IPeripheralFunction MethodResult.of(true)
                }
                val items: List<ItemEntity> = items
                if (items.isEmpty()) {
                    MethodResult.of(null, "Nothing to take")
                }
                var requiredQuantity = requiredQuantityArg
                for (entity in items) {
                    requiredQuantity -= suckItem(entity, requiredQuantity)
                    if (requiredQuantity <= 0) {
                        break
                    }
                }
                return@IPeripheralFunction MethodResult.of(true)
            })
    }
}