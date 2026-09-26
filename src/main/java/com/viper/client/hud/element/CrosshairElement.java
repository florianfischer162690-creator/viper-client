package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class CrosshairElement {

    private static final int COLOR_ACCENT     = 0xFFA855F7;
    private static final int COLOR_ACCENT_HL  = 0xFFC084FC;
    private static final int COLOR_TARGET     = 0xFFFF3B30;
    private static final int COLOR_BG         = 0xAA000000;

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.options.hudHidden) return;
        if (mc.currentScreen != null) return;

        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;

        // vanilla crosshair entfernen wäre schöner — machen wir erstmal nicht
        // custom crosshair drüber zeichnen

        // target-indicator prüfen
        boolean targetLocked = false;
        if (Config.showTargetIndicator) {
            HitResult hit = mc.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult eHit = (EntityHitResult) hit;
                Entity e = eHit.getEntity();
                if (e != mc.player && (e.isAlive())) {
                    targetLocked = true;
                }
            }
        }

        if (Config.showCustomCrosshair) {
            int color = targetLocked ? COLOR_TARGET : COLOR_ACCENT;

            // 4 striche
            int len = 5;
            int gap = 3;
            int thick = 1;

            // oben
            context.fill(cx - thick, cy - gap - len, cx + thick + 1, cy - gap, color);
            // unten
            context.fill(cx - thick, cy + gap, cx + thick + 1, cy + gap + len, color);
            // links
            context.fill(cx - gap - len, cy - thick, cx - gap, cy + thick + 1, color);
            // rechts
            context.fill(cx + gap, cy - thick, cx + gap + len, cy + thick + 1, color);

            // mittelpunkt
            context.fill(cx, cy, cx + 1, cy + 1, color);
        }

        if (targetLocked) {
            // viereck drumrum
            int boxSize = 16;
            int x1 = cx - boxSize;
            int y1 = cy - boxSize;
            int x2 = cx + boxSize;
            int y2 = cy + boxSize;
            int c = COLOR_TARGET;

            // 4 ecken als L-form
            int seg = 5;

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