package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.BalanceTracker;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class BalanceTrackerElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_GOLD  = 0xFFFFD700;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "balancetracker"; }

    @Override
    public boolean isEnabled() { return Config.donutBalanceTracker; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 654; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (!ServerTabs.showDonutTab()) return 0;
        if (mc.player == null) return 0;

        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return 0;
        EntityHitResult eHit = (EntityHitResult) hit;
        Entity e = eHit.getEntity();
        if (!(e instanceof PlayerEntity player)) return 0;
        if (player == mc.player) return 0;

        String name = player.getName().getString();
        long balance = BalanceTracker.getBalance(name);
        if (balance < 0) return 0;

        int accent = Config.getAccent();

        String label = name;
        String value = formatMoney(balance) + "$";

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_GOLD, true);

        return height;
    }

    private String formatMoney(long amount) {
        if (amount >= 1000000000L) return (amount / 1000000000L) + "B";
        if (amount >= 1000000L) return (amount / 1000000L) + "M";
        if (amount >= 1000L) return (amount / 1000L) + "K";
        return String.valueOf(amount);
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("PlayerName") + 6 + mc.textRenderer.getWidth("999M$") + PADDING * 2;
    }
}