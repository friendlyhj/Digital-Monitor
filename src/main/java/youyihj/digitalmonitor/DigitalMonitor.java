package youyihj.digitalmonitor;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import youyihj.digitalmonitor.parts.item.DigitalMonitorItem;

import java.util.Objects;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
@Mod.EventBusSubscriber
public class DigitalMonitor {
    public static Item DIGITAL_MONITOR;


    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(DIGITAL_MONITOR = new DigitalMonitorItem());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(DIGITAL_MONITOR, 0, new ModelResourceLocation(Objects.requireNonNull(DIGITAL_MONITOR.getRegistryName()), "inventory"));
    }

}
