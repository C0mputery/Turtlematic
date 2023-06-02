package site.siredvin.turtlematic
import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.util.text
import site.siredvin.turtlematic.common.setup.Items
import site.siredvin.turtlematic.xplat.ModRecipeIngredients
import site.siredvin.turtlematic.xplat.TurtlematicCommonHooks
import site.siredvin.turtlematic.xplat.TurtlematicPlatform

@Suppress("UNUSED")
object TurtlematicCore {
    const val MOD_ID = "turtlematic"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Items.AUTOMATA_CORE.get().defaultInstance }
            .title(text(MOD_ID, "creative_tab"))
            .displayItems { _, output ->
                TurtlematicPlatform.ITEMS.forEach { output.accept(it.get()) }
                TurtlematicCommonHooks.registerTurtlesInCreativeTab(output)
            }
    }

    fun configure(platform: TurtlematicPlatform, ingredients: ModRecipeIngredients) {
        TurtlematicPlatform.configure(platform)
        ModRecipeIngredients.configure(ingredients)
    }
}
