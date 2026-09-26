package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class WatermarkElement extends HudElement {

    private static final int COLOR_BG = 0x77000000;
    private static final int PADDING = 6;

    @Override
    public String getId() { return "watermark"; }

    @Override
    public boolean isEnabled() { return Config.showWatermark; }

    @Override
    public int getDefaultX() { return 8; }
    @Override
    public int getDefaultY() { return 8; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int accent = Config.getAccent();
        int accentLight = Config.getAccentLight();

        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 600.0) * 0.5 + 0.5);

        String left = "VIPER";
        String right = "V1";
        int lw = mc.textRenderer.getWidth(left);
        int rw = mc.textRenderer.getWidth(right);

        int gap = 5;
        int width = lw + gap + rw + PADDING * 2 + 5;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        int glowAlpha = (int) (pulse * 40 + 30);
        int glowColor = (accent & 0x00FFFFFF) | (glowAlpha << 24);
        context.fill(x - 2, y - 2, x + width + 2, y + height + 2, glowColor);
        context.fill(x - 1, y - 1, x + width + 1, y + height + 1, COLOR_BG);

        context.fill(x, y, x + 3, y + height, accent);

        int shadowColor = (accent & 0x00FFFFFF) | 0x55000000;
        context.drawText(mc.textRenderer, "§l" + left, x + PADDING + 3 + 1, y + PADDING + 1, shadowColor, false);
        context.drawText(mc.textRenderer, right, x + PADDING + 3 + lw + gap + 1, y + PADDING + 1, shadowColor, false);

        context.drawText(mc.textRenderer, "§l" + left, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, right, x + PADDING + 3 + lw + gap, y + PADDING, accentLight, true);

        int pulseAlpha = (int) (pulse * 200 + 55);
        int pulseAccent = (accentLight & 0x00FFFFFF) | (pulseAlpha << 24);
        context.fill(x, y, x + 3, y + 2, pulseAccent);
        context.fill(x, y + height - 2, x + 3, y + height, pulseAccent);

        return height + 4;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("VIPER") + 5 + mc.textRenderer.getWidth("V1") + PADDING * 2 + 5 + 4;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.textRenderer.fontHeight + PADDING * 2 + 4;
    }
}