package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class CrosshairElement {

    private static long lastHitTime = 0;
    private static final long HITMARKER_DURATION = 250;

    public static void triggerHitMarker() {
        lastHitTime = System.currentTimeMillis();
    }

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.options.hudHidden) return;
        if (mc.currentScreen != null) return;

        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;

        LivingEntity targetEntity = null;
        if (Config.showTargetIndicator) {
            HitResult hit = mc.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult eHit = (EntityHitResult) hit;
                Entity e = eHit.getEntity();
                if (e != mc.player && e.isAlive() && e instanceof LivingEntity le) {
                    targetEntity = le;
                }
            }
        }

        boolean targetLocked = targetEntity != null;

        // custom crosshair je nach preset
        if (Config.showCustomCrosshair) {
            int color = targetLocked ? Config.getEffectiveTargetColor() : Config.getEffectiveCrosshairColor();
            drawCrosshair(context, cx, cy, color, Config.crosshairPreset);
        }

        if (targetLocked) {
            drawTargetIndicator(context, cx, cy, mc, targetEntity);
        }

        // hit-marker
        if (Config.showHitMarker && lastHitTime > 0) {
            long elapsed = System.currentTimeMillis() - lastHitTime;
            if (elapsed < HITMARKER_DURATION) {
                float fade = 1.0f - (float) elapsed / HITMARKER_DURATION;
                int alpha = (int) (fade * 255);
                if (alpha < 0) alpha = 0;
                if (alpha > 255) alpha = 255;
                int baseColor = Config.getEffectiveHitMarkerColor();
                int col = (baseColor & 0x00FFFFFF) | (alpha << 24);
                int size = 6;
                int len = 4;
                for (int i = 0; i < len; i++) {
                    context.fill(cx - size - i, cy - size - i, cx - size - i + 1, cy - size - i + 1, col);
                    context.fill(cx + size + i, cy - size - i, cx + size + i + 1, cy - size - i + 1, col);
                    context.fill(cx - size - i, cy + size + i, cx - size - i + 1, cy + size + i + 1, col);
                    context.fill(cx + size + i, cy + size + i, cx + size + i + 1, cy + size + i + 1, col);
                }
            }
        }
    }

    /* ═══════════ CROSSHAIR PRESETS ═══════════ */

    private static void drawCrosshair(DrawContext context, int cx, int cy, int color, String preset) {
        switch (preset) {
            case "classic":
                drawCross(context, cx, cy, color, 4, 2, 1);
                break;
            case "dot":
                context.fill(cx - 1, cy - 1, cx + 2, cy + 2, color);
                break;
            case "cross":
                drawCross(context, cx, cy, color, 4, 2, 1);
                context.fill(cx, cy, cx + 1, cy + 1, color);
                break;
            case "big":
                drawCross(context, cx, cy, color, 8, 2, 1);
                break;
            case "circle":
                drawCircle(context, cx, cy, 6, color);
                break;
            case "circle-dot":
                drawCircle(context, cx, cy, 6, color);
                context.fill(cx, cy, cx + 1, cy + 1, color);
                break;
            case "t-style":
                // oben, links, rechts (kein unten)
                context.fill(cx, cy - 2 - 4, cx + 1, cy - 2, color);
                context.fill(cx - 2 - 4, cy, cx - 2, cy + 1, color);
                context.fill(cx + 2, cy, cx + 2 + 4, cy + 1, color);
                break;
            case "bracket":
                // [ ]
                context.fill(cx - 6, cy - 5, cx - 5, cy + 5, color);
                context.fill(cx - 6, cy - 5, cx - 2, cy - 4, color);
                context.fill(cx - 6, cy + 4, cx - 2, cy + 5, color);
                context.fill(cx + 5, cy - 5, cx + 6, cy + 5, color);
                context.fill(cx + 2, cy - 5, cx + 6, cy - 4, color);
                context.fill(cx + 2, cy + 4, cx + 6, cy + 5, color);
                break;
            case "plus":
                context.fill(cx - 5, cy, cx + 6, cy + 1, color);
                context.fill(cx, cy - 5, cx + 1, cy + 6, color);
                break;
            case "x":
                for (int i = -5; i <= 5; i++) {
                    context.fill(cx + i, cy + i, cx + i + 1, cy + i + 1, color);
                    context.fill(cx + i, cy - i, cx + i + 1, cy - i + 1, color);
                }
                break;
            case "corner":
                // 4 ecken
                context.fill(cx - 6, cy - 6, cx - 3, cy - 5, color);
                context.fill(cx - 6, cy - 6, cx - 5, cy - 3, color);
                context.fill(cx + 3, cy - 6, cx + 6, cy - 5, color);
                context.fill(cx + 5, cy - 6, cx + 6, cy - 3, color);
                context.fill(cx - 6, cy + 5, cx - 3, cy + 6, color);
                context.fill(cx - 6, cy + 3, cx - 5, cy + 6, color);
                context.fill(cx + 3, cy + 5, cx + 6, cy + 6, color);
                context.fill(cx + 5, cy + 3, cx + 6, cy + 6, color);
                break;
            case "minimal":
                context.fill(cx, cy - 1, cx + 1, cy + 2, color);
                break;
            default:
                drawCross(context, cx, cy, color, 4, 2, 1);
        }
    }

    private static void drawCross(DrawContext context, int cx, int cy, int color, int len, int gap, int thickness) {
        context.fill(cx, cy - gap - len, cx + thickness, cy - gap, color);
        context.fill(cx, cy + gap, cx + thickness, cy + gap + len, color);
        context.fill(cx - gap - len, cy, cx - gap, cy + thickness, color);
        context.fill(cx + gap, cy, cx + gap + len, cy + thickness, color);
    }

    private static void drawCircle(DrawContext context, int cx, int cy, int radius, int color) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int d = i * i + j * j;
                if (d <= radius * radius && d >= (radius - 1) * (radius - 1)) {
                    context.fill(cx + i, cy + j, cx + i + 1, cy + j + 1, color);
                }
            }
        }
    }

    /* ═══════════ TARGET INDICATOR ═══════════ */

    private static void drawTargetIndicator(DrawContext context, int cx, int cy, MinecraftClient mc, LivingEntity target) {
        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 250.0) * 0.5 + 0.5);
        int boxSize = (int) (10 + pulse * 2);
        int seg = 4;
        int x1 = cx - boxSize;
        int y1 = cy - boxSize;
        int x2 = cx + boxSize;
        int y2 = cy + boxSize;
        int c = Config.getEffectiveTargetColor();

        context.fill(x1, y1, x1 + seg, y1 + 1, c);
        context.fill(x1, y1, x1 + 1, y1 + seg, c);
        context.fill(x2 - seg, y1, x2, y1 + 1, c);
        context.fill(x2 - 1, y1, x2, y1 + seg, c);
        context.fill(x1, y2 - 1, x1 + seg, y2, c);
        context.fill(x1, y2 - seg, x1 + 1, y2, c);
        context.fill(x2 - seg, y2 - 1, x2, y2, c);
        context.fill(x2 - 1, y2 - seg, x2, y2, c);

        if (Config.showHealthIndicator) {
            float health = target.getHealth();
            float maxHealth = target.getMaxHealth();
            float ratio = maxHealth > 0 ? health / maxHealth : 0;
            if (ratio > 1f) ratio = 1f;
            if (ratio < 0f) ratio = 0f;
            int barW = 50;
            int barH = 4;
            int barX = cx - barW / 2;
            int barY = cy + boxSize + 12;
            int bgColor = 0xCC000000;
            int fgColor = Config.getEffectiveHealthColor();
            context.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xCC0a0a0f);
            context.fill(barX, barY, barX + barW, barY + barH, bgColor);
            int fillW = (int) (barW * ratio);
            if (fillW > 0) context.fill(barX, barY, barX + fillW, barY + barH, fgColor);
            context.fill(barX, barY, barX + barW, barY + 1, 0xFFFFFFFF);
            context.fill(barX, barY + barH - 1, barX + barW, barY + barH, 0xFFFFFFFF);
            context.fill(barX, barY, barX + 1, barY + barH, 0xFFFFFFFF);
            context.fill(barX + barW - 1, barY, barX + barW, barY + barH, 0xFFFFFFFF);
            String hp = (int) Math.ceil(health) + "/" + (int) maxHealth;
            int tw = mc.textRenderer.getWidth(hp);
            context.drawText(mc.textRenderer, hp, cx - tw / 2, barY - 10, 0xFFFFFFFF, true);
        }
    }
}