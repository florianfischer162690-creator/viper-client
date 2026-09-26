package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.AttackTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ReachElement extends HudElement {

    private static final int COLOR_LABEL = 0xFFA855F7;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_GOOD  = 0xFF23A55A;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "reach"; }

    @Override
    public boolean isEnabled() {
        return Config.showReach;
    }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 120; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (!AttackTracker.isReachVisible()) return 0;

        float reach = AttackTracker.getReach();
        String label = "REACH";
        String value = String.format("%.2f", reach);

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, COLOR_LABEL);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, COLOR_LABEL, true);

        // grün bei normal (3.0), gelb bei hoch (3.5+), weiß bei kurz
        int valueColor;
        if (reach >= 3.5f) valueColor = COLOR_WARN;
        else if (reach >= 2.5f) valueColor = COLOR_GOOD;
        else valueColor = COLOR_VALUE;

        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("REACH") + 6 + mc.textRenderer.getWidth("9.99") + PADDING * 2;
    }
}