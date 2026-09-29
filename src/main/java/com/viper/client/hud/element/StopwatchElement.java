package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class StopwatchElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_ACCENT = 0xFF23A55A;
    private static final int PADDING = 3;

    private static long startTime = 0L;
    private static long pausedTime = 0L;
    private static boolean running = false;

    public static void toggle() {
        if (running) {
            pausedTime = System.currentTimeMillis() - startTime;
            running = false;
        } else {
            startTime = System.currentTimeMillis() - pausedTime;
            running = true;
        }
    }

    public static void reset() {
        startTime = 0L;
        pausedTime = 0L;
        running = false;
    }

    public static boolean isRunning() { return running; }

    private static long elapsed() {
        if (running) return System.currentTimeMillis() - startTime;
        return pausedTime;
    }

    private static String format(long ms) {
        long totalSec = ms / 1000;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;
        long cs = (ms % 1000) / 10;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d.%02d", m, s, cs);
    }

    @Override
    public String getId() { return "stopwatch"; }

    @Override
    public boolean isEnabled() { return Config.showStopwatch; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 400; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int accent = Config.getAccent();

        String label = "SW";
        String value = format(elapsed());

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, running ? COLOR_ACCENT : accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_VALUE, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("SW") + 6 + mc.textRenderer.getWidth("00:00.00") + PADDING * 2;
    }
}