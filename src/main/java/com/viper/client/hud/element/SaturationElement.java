package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SaturationElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "saturation"; }

    @Override
    public boolean isEnabled() { return Config.showSaturationBar; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 238; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        float sat = mc.player.getHungerManager().getSaturationLevel();
        String label = "SAT";
        String value = String.format("%.1f", sat);

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_VALUE, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("SAT") + 6 + mc.textRenderer.getWidth("20.0") + PADDING * 2;
    }
}