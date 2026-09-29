package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DamageTintRenderer {

    private static float lastHealth = 20.0f;
    private static long lastDamageTime = 0L;
    private static final long TINT_DURATION = 500L;

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            try {
                render(context);
            } catch (Throwable ignored) {}
        });
    }

    private static void render(DrawContext context) {
        if (!Config.damageTint) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        float health = mc.player.getHealth();

        if (health < lastHealth) {
            lastDamageTime = System.currentTimeMillis();
        }
        lastHealth = health;

        long elapsed = System.currentTimeMillis() - lastDamageTime;
        if (elapsed > TINT_DURATION) return;

        float fade = 1.0f - (float) elapsed / TINT_DURATION;
        int alpha = (int) (fade * Config.damageTintIntensity);
        if (alpha <= 0) return;
        if (alpha > 180) alpha = 180;

        int color = (alpha << 24) | 0x00FF0000;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        int thickness = 20;
        context.fill(0, 0, w, thickness, color);
        context.fill(0, h - thickness, w, h, color);
        context.fill(0, 0, thickness, h, color);
        context.fill(w - thickness, 0, w, h, color);
    }
}