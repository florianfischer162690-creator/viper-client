package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.TotemTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TotemPopElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int PADDING = 3;
    private static final double MAX_DISTANCE = 40.0;
    private static final double MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

    @Override
    public String getId() { return "totempop"; }

    @Override
    public boolean isEnabled() { return Config.showTotemPop; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 184; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.world == null || mc.player == null) return 0;

        int accent = Config.getAccent();

        double myX = mc.player.getX();
        double myY = mc.player.getY();
        double myZ = mc.player.getZ();

        List<PlayerEntry> entries = new ArrayList<>();

        for (AbstractClientPlayerEntity other : mc.world.getPlayers()) {
            if (other == mc.player) continue;
            double dx = other.getX() - myX;
            double dy = other.getY() - myY;
            double dz = other.getZ() - myZ;
            double dist2 = dx * dx + dy * dy + dz * dz;
            if (dist2 > MAX_DISTANCE_SQ) continue;

            int count = TotemTracker.getPops(other.getUuid());
            if (count > 0) {
                entries.add(new PlayerEntry(other.getName().getString(), count));
            }
        }

        entries.sort(Comparator.comparingInt((PlayerEntry e) -> e.count).reversed());
        if (entries.size() > 3) entries = entries.subList(0, 3);

        if (entries.isEmpty()) return 0;

        int lineH = mc.textRenderer.fontHeight + 2;
        int width = 0;
        for (PlayerEntry e : entries) {
            String line = e.name + " x" + e.count;
            int w = mc.textRenderer.getWidth(line);
            if (w > width) width = w;
        }
        width += PADDING * 2 + 5;

        int totalH = lineH * entries.size() + PADDING * 2 + mc.textRenderer.fontHeight + 2;

        context.fill(x, y, x + width, y + totalH, COLOR_BG);
        context.fill(x, y, x + 2, y + totalH, accent);

        context.drawText(mc.textRenderer, "§lTOTEM POPS", x + PADDING + 4, y + PADDING, accent, true);

        int lineY = y + PADDING + mc.textRenderer.fontHeight + 3;
        for (PlayerEntry e : entries) {
            context.drawText(mc.textRenderer, e.name, x + PADDING + 4, lineY, COLOR_VALUE, true);
            String count = "x" + e.count;
            int cw = mc.textRenderer.getWidth(count);
            context.drawText(mc.textRenderer, count, x + width - cw - PADDING - 3, lineY, accent, true);
            lineY += lineH;
        }

        return totalH;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("TOTEM POPS") + PADDING * 2 + 20;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.textRenderer.fontHeight * 4 + 8;
    }

    private static class PlayerEntry {
        String name;
        int count;
        PlayerEntry(String n, int c) { this.name = n; this.count = c; }
    }
}