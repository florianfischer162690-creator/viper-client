package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class AttackIndicatorElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_CRIT  = 0xFFFF3B30;
    private static final int COLOR_SWEEP = 0xFF3498DB;
    private static final int COLOR_READY = 0xFF23A55A;
    private static final int PADDING = 3;

    private static long lastAttackTime = 0L;
    private static final long FADE = 1500L;
    private static boolean wasCrit = false;
    private static boolean wasSweep = false;

    public static void registerAttack(boolean crit, boolean sweep) {
        lastAttackTime = System.currentTimeMillis();
        wasCrit = crit;
        wasSweep = sweep;
    }

    @Override
    public String getId() { return "attackindicator"; }

    @Override
    public boolean isEnabled() { return Config.showAttackIndicator; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 564; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        // attack-cooldown anzeigen (0.0 - 1.0)
        float cooldown = mc.player.getAttackCooldownProgress(0.0f);
        boolean ready = cooldown >= 1.0f;

        String label = "ATK";
        String value;
        int valueColor;

        long elapsed = System.currentTimeMillis() - lastAttackTime;
        if (elapsed < FADE && lastAttackTime > 0) {
            if (wasCrit) {
                value = "CRIT";
                valueColor = COLOR_CRIT;
            } else if (wasSweep) {
                value = "SWEEP";
                valueColor = COLOR_SWEEP;
            } else {
                value = String.format("%d%%", (int) (cooldown * 100));
                valueColor = ready ? COLOR_READY : COLOR_VALUE;
            }
        } else {
            value = ready ? "READY" : String.format("%d%%", (int) (cooldown * 100));
            valueColor = ready ? COLOR_READY : COLOR_VALUE;
        }

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2 + 4;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        // cooldown-bar unten
        int barY = y + height - 3;
        int barW = width - 6;
        context.fill(x + 3, barY, x + 3 + barW, barY + 2, 0x44000000);
        int fillW = (int) (barW * cooldown);
        if (fillW > 0) {
            int barColor = ready ? COLOR_READY : accent;
            context.fill(x + 3, barY, x + 3 + fillW, barY + 2, barColor);
        }

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("ATK") + 6 + mc.textRenderer.getWidth("SWEEP") + PADDING * 2;
    }
}