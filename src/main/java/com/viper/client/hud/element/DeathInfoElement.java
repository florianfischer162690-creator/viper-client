package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DeathInfoElement extends HudElement {

    private static final int COLOR_BG    = 0xCC1a0505;
    private static final int COLOR_VALUE = 0xFFFF5555;
    private static final int COLOR_MUTED = 0xFFE6E6E6;
    private static final int PADDING = 4;

    private static boolean hasDeath = false;
    private static double deathX = 0;
    private static double deathY = 0;
    private static double deathZ = 0;
    private static String deathDim = "";
    private static String deathCause = "";

    public static void registerDeath(double x, double y, double z, String dim, String cause) {
        hasDeath = true;
        deathX = x;
        deathY = y;
        deathZ = z;
        deathDim = dim != null ? dim : "";
        deathCause = cause != null ? cause : "";
    }

    public static void clear() {
        hasDeath = false;
    }

    @Override
    public String getId() { return "deathinfo"; }

    @Override
    public boolean isEnabled() { return Config.showDeathInfo; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 582; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (!hasDeath) return 0;

        int xx = (int) deathX;
        int yy = (int) deathY;
        int zz = (int) deathZ;

        String dim = deathDim;
        if (dim.contains(":")) {
            dim = dim.substring(dim.indexOf(":") + 1);
        }

        String line1 = "DEATH: " + xx + " " + yy + " " + zz;
        String line2 = dim + (deathCause.isEmpty() ? "" : " · " + deathCause);

        int line1W = mc.textRenderer.getWidth(line1);
        int line2W = mc.textRenderer.getWidth(line2);
        int width = Math.max(line1W, line2W) + PADDING * 2 + 4;
        int lineH = mc.textRenderer.fontHeight + 2;
        int height = lineH * 2 + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 3, y + height, COLOR_VALUE);

        context.drawText(mc.textRenderer, line1, x + PADDING + 3, y + PADDING, COLOR_VALUE, true);
        context.drawText(mc.textRenderer, line2, x + PADDING + 3, y + PADDING + lineH, COLOR_MUTED, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return 200;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.textRenderer.fontHeight * 2 + 12;
    }
}