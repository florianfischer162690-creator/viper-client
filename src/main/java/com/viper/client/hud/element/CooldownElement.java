package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class CooldownElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_READY = 0xFF23A55A;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "cooldown"; }

    @Override
    public boolean isEnabled() { return Config.showCooldown; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 528; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        ItemStack main = mc.player.getStackInHand(Hand.MAIN_HAND);
        ItemStack off  = mc.player.getStackInHand(Hand.OFF_HAND);

        ItemStack tracked = null;
        float progress = 0f;
        float remaining = 0f;

        if (mc.player.getItemCooldownManager().isCoolingDown(main)) {
            tracked = main;
            progress = mc.player.getItemCooldownManager().getCooldownProgress(main, 0f);
            remaining = (1f - progress) * 100f;
        } else if (mc.player.getItemCooldownManager().isCoolingDown(off)) {
            tracked = off;
            progress = mc.player.getItemCooldownManager().getCooldownProgress(off, 0f);
            remaining = (1f - progress) * 100f;
        }

        String label = "CD";
        String value;

        if (tracked == null) {
            if (!Config.showCooldownIdle) return 0;
            value = "ready";
        } else {
            value = tracked.getName().getString();
            if (value.length() > 14) value = value.substring(0, 13) + "…";
        }

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2 + (tracked != null ? 5 : 0);

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING,
                tracked == null ? COLOR_READY : COLOR_VALUE, true);

        if (tracked != null) {
            int barY = y + height - 3;
            int barW = width - 6;
            context.fill(x + 3, barY, x + 3 + barW, barY + 2, 0x44000000);
            int fillW = (int) (barW * progress);
            if (fillW > 0) context.fill(x + 3, barY, x + 3 + fillW, barY + 2, accent);
        }

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("CD") + 6 + mc.textRenderer.getWidth("Diamond Sword") + PADDING * 2;
    }
}