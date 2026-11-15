package dev.voidskins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkinScreen extends Screen {
    private static final int TOP = 40;
    private static final int ROW = 26;

    private final Screen parent;

    public SkinScreen(Screen parent) {
        super(Component.literal("Void Skins"));
        this.parent = parent;
    }

    private int colX(int k) {
        return this.width / 2 + (k == 0 ? -165 : 5);
    }

    private static String cap(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static Component label(String itemPath) {
        String[] parts = itemPath.split("_");
        String skin = SkinConfig.get(itemPath);
        return Component.literal(cap(parts[0]) + " " + cap(parts[1]) + ": "
                + ("default".equals(skin) ? "Default" : cap(skin)));
    }

    private static void cycle(String itemPath) {
        List<String> options = new ArrayList<>();
        options.add("default");
        options.addAll(SkinConfig.SKINS);
        int idx = options.indexOf(SkinConfig.get(itemPath));
        SkinConfig.set(itemPath, options.get((idx + 1) % options.size()));
    }

    @Override
    protected void init() {
        String[] mats = ClientModEvents.MATS;
        String[] kinds = ClientModEvents.KINDS;
        for (int k = 0; k < kinds.length; k++) {
            for (int m = 0; m < mats.length; m++) {
                final String id = mats[m] + "_" + kinds[k];
                int y = TOP + m * ROW;
                this.addRenderableWidget(Button.builder(label(id), b -> {
                    cycle(id);
                    b.setMessage(label(id));
                }).bounds(colX(k) + 22, y, 138, 20).build());
            }
        }
        int by = TOP + mats.length * ROW + 8;
        this.addRenderableWidget(Button.builder(Component.literal("Reset all"), b -> {
            SkinConfig.resetAll();
            this.rebuildWidgets();
        }).bounds(this.width / 2 - 155, by, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.width / 2 + 5, by, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFF);
        g.drawCenteredString(this.font, Component.literal("Click a button to cycle the skin"),
                this.width / 2, 26, 0xAAAAAA);
        String[] mats = ClientModEvents.MATS;
        String[] kinds = ClientModEvents.KINDS;
        for (int k = 0; k < kinds.length; k++) {
            for (int m = 0; m < mats.length; m++) {
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath("minecraft", mats[m] + "_" + kinds[k])));
                g.renderItem(stack, colX(k) + 2, TOP + m * ROW + 2);
            }
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
