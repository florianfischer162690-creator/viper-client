package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.RtpTracker;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class RtpTimerElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_READY = 0xFF23A55A;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "rtptimer"; }

    @Override
    public boolean isEnabled() { return Config.showRtpTimer; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 690; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (!ServerTabs.showHugoTab()) return 0;

        int accent = Config.getAccent();

        long remaining = RtpTracker.getRemainingMs();
        boolean ready = remaining <= 0L;

        String label = "RTP";
        String value;
        int valueColor;

        if (ready) {
            value = "READY";
            valueColor = COLOR_READY;
        } else {
            long sec = remaining / 1000;
            long min = sec / 60;
            long s = sec % 60;
            if (min > 0) {
                value = min + "m " + s + "s";
            } else {
                value = sec + "s";
            }
            valueColor = COLOR_VALUE;
        }

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2 + 4;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        // cooldown bar
        if (!ready) {
            long total = Math.max(remaining, 1);
            // bar ist nicht exakt, wir kennen nicht den initialwert — placeholder
            int barY = y + height - 3;
            int barW = width - 6;
            context.fill(x + 3, barY, x + 3 + barW, barY + 2, 0x44000000);
            context.fill(x + 3, barY, x + 3 + barW, barY + 2, accent);
        }

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("RTP") + 6 + mc.textRenderer.getWidth("99m 59s") + PADDING * 2;
    }
}