package dev.voidskins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = VoidSkins.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.voidskins.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.voidskins");

    public static final String[] MATS = {"wooden", "stone", "iron", "golden", "diamond", "netherite"};
    public static final String[] KINDS = {"sword", "pickaxe"};

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        SkinConfig.load();
        event.enqueueWork(() -> {
            ResourceLocation prop = ResourceLocation.fromNamespaceAndPath(VoidSkins.MODID, "skin");
            for (String mat : MATS) {
                for (String kind : KINDS) {
                    final String path = mat + "_" + kind;
                    Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", path));
                    ItemProperties.register(item, prop, (stack, level, entity, seed) -> SkinConfig.index(path));
                }
            }
        });
    }
}
