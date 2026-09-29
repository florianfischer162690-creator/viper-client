package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.StaffTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class AdminHudElement extends HudElement {

    private static final int COLOR_BG = 0xCC2a1a05;
    private static final int PADDING = 4;

    @Override
    public String getId() { return "adminhud"; }

    @Override
    public boolean isEnabled() { return Config.donutAdminHud; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 330; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        List<String> staff = StaffTracker.getStaffOnline();
        if (staff.isEmpty()) return 0;

        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 300.0) * 0.5 + 0.5);
        int alpha = (int) (140 + pulse * 115);

        int color = 0xFFFFAA00;
        int borderCol = (color & 0x00FFFFFF) | (alpha << 24);

        String text = "⚠ STAFF: " + String.join(", ", staff);
        int tw = mc.textRenderer.getWidth(text);
        int width = tw + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + width, y + 1, borderCol);
        context.fill(x, y + height - 1, x + width, y + height, borderCol);
        context.fill(x, y, x + 1, y + height, borderCol);
        context.fill(x + width - 1, y, x + width, y + height, borderCol);

        int textCol = 0xFFFFFF | (alpha << 24);
        context.drawText(mc.textRenderer, text, x + PADDING, y + PADDING, textCol, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("⚠ STAFF: PLAYER_NAME_LONG, PLAYER2") + PADDING * 2;
    }
}