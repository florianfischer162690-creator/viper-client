package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_GOOD  = 0xFF23A55A;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BAD   = 0xFFED4245;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "ping"; }

    @Override
    public boolean isEnabled() { return Config.showPing; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 76; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null || mc.getNetworkHandler() == null) return 0;
        int accent = Config.getAccent();

        int ping = 0;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry != null) ping = entry.getLatency();

        String label = "PING";
        String value = ping + "ms";

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);

        int valueColor;
        if (ping <= 60) valueColor = COLOR_GOOD;
        else if (ping <= 150) valueColor = COLOR_WARN;
        else valueColor = COLOR_BAD;

        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("PING") + 6 + mc.textRenderer.getWidth("9999ms") + PADDING * 2;
    }
}