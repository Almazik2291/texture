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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinScreen extends Screen {
    private static final int TOP = 40;
    private static final int ROW = 26;

    private final Screen parent;
    private final Map<String, String> pending = new HashMap<>(SkinConfig.snapshot());

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

    private String current(String itemPath) {
        return pending.getOrDefault(itemPath, "default");
    }

    private Component label(String itemPath) {
        String[] parts = itemPath.split("_");
        String skin = current(itemPath);
        return Component.literal(cap(parts[0]) + " " + cap(parts[1]) + ": "
                + ("default".equals(skin) ? "Default" : cap(skin)));
    }

    private void cycle(String itemPath) {
        List<String> options = new ArrayList<>();
        options.add("default");
        options.addAll(SkinConfig.SKINS);
        int idx = options.indexOf(current(itemPath));
        String next = options.get((idx + 1) % options.size());
        if ("default".equals(next)) pending.remove(itemPath);
        else pending.put(itemPath, next);
    }

    @Override
    protected void init() {
        for (int k = 0; k < SkinConfig.KINDS.length; k++) {
            for (int m = 0; m < SkinConfig.MATS.length; m++) {
                final String id = SkinConfig.MATS[m] + "_" + SkinConfig.KINDS[k];
                int y = TOP + m * ROW;
                this.addRenderableWidget(Button.builder(label(id), b -> {
                    cycle(id);
                    b.setMessage(label(id));
                }).bounds(colX(k) + 22, y, 138, 20).build());
            }
        }
        int by = TOP + SkinConfig.MATS.length * ROW + 8;
        this.addRenderableWidget(Button.builder(Component.literal("Reset all"), b -> {
            pending.clear();
            this.rebuildWidgets();
        }).bounds(this.width / 2 - 155, by, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Apply (reload)"), b -> {
            SkinConfig.replaceAll(pending);
            PackApplier.apply();
        }).bounds(this.width / 2 - 50, by, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.width / 2 + 55, by, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        g.drawCenteredString(this.font, Component.literal("Click to cycle skins, then press Apply"),
                this.width / 2, 24, 0xAAAAAA);
        for (int k = 0; k < SkinConfig.KINDS.length; k++) {
            for (int m = 0; m < SkinConfig.MATS.length; m++) {
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(
                        ResourceLocation.fromNamespaceAndPath("minecraft",
                                SkinConfig.MATS[m] + "_" + SkinConfig.KINDS[k])));
                g.renderItem(stack, colX(k) + 2, TOP + m * ROW + 2);
            }
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
