package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SpeedElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    private static double lastX = 0;
    private static double lastZ = 0;
    private static long lastTime = 0;
    private static double speed = 0;

    @Override
    public String getId() { return "speed"; }

    @Override
    public boolean isEnabled() { return Config.showSpeed; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 166; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        long now = System.currentTimeMillis();
        if (lastTime > 0) {
            double dx = mc.player.getX() - lastX;
            double dz = mc.player.getZ() - lastZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            double dt = (now - lastTime) / 1000.0;
            if (dt > 0) speed = dist / dt;
        }
        lastX = mc.player.getX();
        lastZ = mc.player.getZ();
        lastTime = now;

        String label = "SPD";
        String value = String.format("%.2f", speed);

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
        return mc.textRenderer.getWidth("SPD") + 6 + mc.textRenderer.getWidth("9.99") + PADDING * 2;
    }
}