package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;

public class AbsorptionElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;
    private static final int GOLD = 0xFFFFC107;

    @Override
    public String getId() { return "absorption"; }

    @Override
    public boolean isEnabled() { return Config.showAbsorptionHearts; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 256; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;

        float absorp = mc.player.getAbsorptionAmount();
        if (absorp <= 0) return 0;

        int accent = Config.getAccent();

        String label = "ABS";
        String value = "+" + (int) absorp;

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, GOLD);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, GOLD, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("ABS") + 6 + mc.textRenderer.getWidth("+20") + PADDING * 2;
    }
}