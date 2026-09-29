package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class MemoryElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BAD   = 0xFFED4245;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "memory"; }

    @Override
    public boolean isEnabled() { return Config.showMemory; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 474; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int accent = Config.getAccent();

        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();

        long usedMb = used / (1024L * 1024L);
        long maxMb = max / (1024L * 1024L);
        int pct = max > 0 ? (int) ((used * 100L) / max) : 0;

        String label = "RAM";
        String value = usedMb + "/" + maxMb + "MB";

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);

        int valueColor;
        if (pct >= 90) valueColor = COLOR_BAD;
        else if (pct >= 70) valueColor = COLOR_WARN;
        else valueColor = COLOR_VALUE;

        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("RAM") + 6 + mc.textRenderer.getWidth("9999/9999MB") + PADDING * 2;
    }
}