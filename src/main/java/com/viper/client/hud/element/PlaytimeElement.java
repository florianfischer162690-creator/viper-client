package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class PlaytimeElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int PADDING = 3;

    private static long sessionStart = 0L;

    @Override
    public String getId() { return "playtime"; }

    @Override
    public boolean isEnabled() { return Config.showPlaytime; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 456; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int accent = Config.getAccent();

        if (sessionStart == 0L) sessionStart = System.currentTimeMillis();
        long ms = System.currentTimeMillis() - sessionStart;
        long totalSec = ms / 1000;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;

        String label = "PT";
        String value = String.format("%02d:%02d:%02d", h, m, s);

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
        return mc.textRenderer.getWidth("PT") + 6 + mc.textRenderer.getWidth("00:00:00") + PADDING * 2;
    }
}