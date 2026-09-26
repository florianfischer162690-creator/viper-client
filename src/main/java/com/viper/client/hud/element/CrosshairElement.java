package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class CrosshairElement {

    private static final int COLOR_ACCENT     = 0xFFA855F7;
    private static final int COLOR_TARGET     = 0xFFFF3B30;

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.options.hudHidden) return;
        if (mc.currentScreen != null) return;

        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;

        // target check
        boolean targetLocked = false;
        if (Config.showTargetIndicator) {
            HitResult hit = mc.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult eHit = (EntityHitResult) hit;
                Entity e = eHit.getEntity();
                if (e != mc.player && e.isAlive()) {
                    targetLocked = true;
                }
            }
        }

        if (Config.showCustomCrosshair) {
            int color = targetLocked ? COLOR_TARGET : COLOR_ACCENT;

            // 4 dünne striche
            int len = 4;
            int gap = 2;

            // oben
            context.fill(cx, cy - gap - len, cx + 1, cy - gap, color);
            // unten
            context.fill(cx, cy + gap, cx + 1, cy + gap + len, color);
            // links
            context.fill(cx - gap - len, cy, cx - gap, cy + 1, color);
            // rechts
            context.fill(cx + gap, cy, cx + gap + len, cy + 1, color);
        }

        if (targetLocked) {
            // puls-wert (sinus, 0..1)
            long t = System.currentTimeMillis();
            float pulse = (float) (Math.sin(t / 250.0) * 0.5 + 0.5);

            // größe basiert auf puls (leicht)
            int boxSize = (int) (10 + pulse * 2); // 10..12
            int seg = 4;

            int x1 = cx - boxSize;
            int y1 = cy - boxSize;
            int x2 = cx + boxSize;
            int y2 = cy + boxSize;
            int c = COLOR_TARGET;

            // 4 ecken (L-form), dünn
            // oben links
            context.fill(x1, y1, x1 + seg, y1 + 1, c);
            context.fill(x1, y1, x1 + 1, y1 + seg, c);
            // oben rechts
            context.fill(x2 - seg, y1, x2, y1 + 1, c);
            context.fill(x2 - 1, y1, x2, y1 + seg, c);
            // unten links
            context.fill(x1, y2 - 1, x1 + seg, y2, c);
            context.fill(x1, y2 - seg, x1 + 1, y2, c);
            // unten rechts
            context.fill(x2 - seg, y2 - 1, x2, y2, c);
            context.fill(x2 - 1, y2 - seg, x2, y2, c);
        }
    }
}