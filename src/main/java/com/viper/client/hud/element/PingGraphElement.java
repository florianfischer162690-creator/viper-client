package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayDeque;
import java.util.Deque;

public class PingGraphElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_LINE  = 0xFF23A55A;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BAD   = 0xFFED4245;
    private static final int PADDING = 3;
    private static final int MAX_SAMPLES = 60;

    private static final Deque<Integer> samples = new ArrayDeque<>();
    private static long lastSample = 0L;

    @Override
    public String getId() { return "pinggraph"; }

    @Override
    public boolean isEnabled() { return Config.showPingGraph; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 510; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null || mc.getNetworkHandler() == null) return 0;
        int accent = Config.getAccent();

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        int ping = entry != null ? entry.getLatency() : 0;

        long now = System.currentTimeMillis();
        if (now - lastSample >= 500L) {
            samples.addLast(ping);
            while (samples.size() > MAX_SAMPLES) samples.pollFirst();
            lastSample = now;
        }

        int labelW = mc.textRenderer.getWidth("PING");
        int width = Math.max(90, labelW + 10 + 80);
        int height = mc.textRenderer.fontHeight + 24;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, "PING", x + PADDING + 3, y + PADDING, accent, true);

        int valueColor;
        if (ping <= 60) valueColor = COLOR_LINE;
        else if (ping <= 150) valueColor = COLOR_WARN;
        else valueColor = COLOR_BAD;

        String pingStr = ping + "ms";
        int pingW = mc.textRenderer.getWidth(pingStr);
        context.drawText(mc.textRenderer, pingStr, x + width - PADDING - 3 - pingW, y + PADDING, valueColor, true);

        int graphX = x + PADDING + 3;
        int graphY = y + mc.textRenderer.fontHeight + 6;
        int graphW = width - PADDING * 2 - 6;
        int graphH = 14;

        context.fill(graphX, graphY, graphX + graphW, graphY + graphH, 0x44000000);

        if (samples.size() >= 2) {
            int maxPing = 100;
            for (int p : samples) if (p > maxPing) maxPing = p;

            Integer[] arr = samples.toArray(new Integer[0]);
            int step = Math.max(1, graphW / Math.max(1, arr.length - 1));

            for (int i = 0; i < arr.length - 1; i++) {
                int p1 = arr[i];
                int p2 = arr[i + 1];
                int y1 = graphY + graphH - (int) ((float) p1 / maxPing * graphH);
                int y2 = graphY + graphH - (int) ((float) p2 / maxPing * graphH);
                if (y1 < graphY) y1 = graphY;
                if (y1 > graphY + graphH - 1) y1 = graphY + graphH - 1;
                if (y2 < graphY) y2 = graphY;
                if (y2 > graphY + graphH - 1) y2 = graphY + graphH - 1;
                int px1 = graphX + i * step;
                int px2 = graphX + (i + 1) * step;
                int col = p2 <= 60 ? COLOR_LINE : (p2 <= 150 ? COLOR_WARN : COLOR_BAD);
                drawLine(context, px1, y1, px2, y2, col);
            }
        }

        return height;
    }

    private void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int safety = 0;
        while (true) {
            context.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            if (++safety > 500) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return 100;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.textRenderer.fontHeight + 24;
    }
}