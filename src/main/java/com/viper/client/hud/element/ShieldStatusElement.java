package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class ShieldStatusElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_ON    = 0xFF23A55A;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "shieldstatus"; }

    @Override
    public boolean isEnabled() { return Config.showShieldStatus; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 274; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return 0;
        EntityHitResult eHit = (EntityHitResult) hit;
        Entity e = eHit.getEntity();
        if (!(e instanceof LivingEntity le)) return 0;

        // prüfen ob die entity gerade schild blockt
        boolean blocking = le.isBlocking();

        String label = "SHIELD";
        String value = blocking ? "UP" : "DOWN";
        int valueColor = blocking ? COLOR_ON : 0xFFFF3B30;

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("SHIELD") + 6 + mc.textRenderer.getWidth("DOWN") + PADDING * 2;
    }
}