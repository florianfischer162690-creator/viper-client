package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;

import java.util.Collection;

public class PotionElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_TEXT  = 0xFFE6E6E6;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "potion"; }

    @Override
    public boolean isEnabled() { return Config.showPotion; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 130; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) return 0;

        int lineH = mc.textRenderer.fontHeight + 2;
        int width = 0;

        for (StatusEffectInstance eff : effects) {
            String name = eff.getEffectType().value().getName().getString();
            String dur = StatusEffectUtil.getDurationText(eff, 1.0f, mc.world.getTickManager().getTickRate()).getString();
            String line = name + " " + dur;
            int w = mc.textRenderer.getWidth(line);
            if (w > width) width = w;
        }
        width += PADDING * 2 + 5;

        int totalH = lineH * effects.size() + PADDING * 2;

        context.fill(x, y, x + width, y + totalH, COLOR_BG);
        context.fill(x, y, x + 2, y + totalH, accent);

        int lineY = y + PADDING;
        for (StatusEffectInstance eff : effects) {
            String name = eff.getEffectType().value().getName().getString();
            String dur = StatusEffectUtil.getDurationText(eff, 1.0f, mc.world.getTickManager().getTickRate()).getString();

            int color = eff.getEffectType().value().getColor();
            int argb = color | 0xFF000000;

            context.drawText(mc.textRenderer, name, x + PADDING + 4, lineY, argb, true);
            int nameW = mc.textRenderer.getWidth(name);
            context.drawText(mc.textRenderer, dur, x + PADDING + 4 + nameW + 4, lineY, COLOR_TEXT, true);

            lineY += lineH;
        }

        return totalH;
    }

    @Override
    public int getWidth(MinecraftClient mc) { return 140; }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.player == null ? 0 : (mc.textRenderer.fontHeight + 2) * Math.max(1, mc.player.getStatusEffects().size()) + PADDING * 2;
    }
}