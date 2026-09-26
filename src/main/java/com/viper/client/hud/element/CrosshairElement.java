package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class CrosshairElement {

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

        if (Config.showCustomCrosshair) {
            int color = targetLocked ? Config.getEffectiveTargetColor() : Config.getEffectiveCrosshairColor();

            int len = 4;
            int gap = 2;

            context.fill(cx, cy - gap - len, cx + 1, cy - gap, color);
            context.fill(cx, cy + gap, cx + 1, cy + gap + len, color);
            context.fill(cx - gap - len, cy, cx - gap, cy + 1, color);
            context.fill(cx + gap, cy, cx + gap + len, cy + 1, color);
        }

        if (targetLocked) {
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

            // health-bar UNTER dem target-box, weit weg (y2 + 30)
            if (Config.showHealthIndicator) {
                float health = targetEntity.getHealth();
                float maxHealth = targetEntity.getMaxHealth();
                float ratio = maxHealth > 0 ? health / maxHealth : 0;
                if (ratio > 1f) ratio = 1f;
                if (ratio < 0f) ratio = 0f;

                int barW = 60;
                int barH = 5;
                int barX = cx - barW / 2;
                int barY = y2 + 30;

                int bgColor = 0xCC000000;
                int fgColor = Config.getEffectiveHealthColor();

                // hintergrund-box (leicht größer)
                context.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xCC0a0a0f);

                // gefüllt
                context.fill(barX, barY, barX + barW, barY + barH, bgColor);
                int fillW = (int) (barW * ratio);
                if (fillW > 0) {
                    context.fill(barX, barY, barX + fillW, barY + barH, fgColor);
                }
                // weiße border-linien
                context.fill(barX, barY, barX + barW, barY + 1, 0xFFFFFFFF);
                context.fill(barX, barY + barH - 1, barX + barW, barY + barH, 0xFFFFFFFF);
                context.fill(barX, barY, barX + 1, barY + barH, 0xFFFFFFFF);
                context.fill(barX + barW - 1, barY, barX + barW, barY + barH, 0xFFFFFFFF);

                // HP-zahl drüber
                String hp = (int) Math.ceil(health) + " / " + (int) maxHealth;
                int tw = mc.textRenderer.getWidth(hp);
                context.drawText(mc.textRenderer, hp, cx - tw / 2 + 1, barY - 11 + 1, 0xAA000000, false);
                context.drawText(mc.textRenderer, hp, cx - tw / 2, barY - 11, 0xFFFFFFFF, true);
            }
        }
    }
}