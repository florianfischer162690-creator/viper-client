package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;

public class TotemWarningElement extends HudElement {

    private static final int COLOR_BG = 0xCC1a0505;
    private static final int PADDING = 4;

    @Override
    public String getId() { return "totemwarning"; }

    @Override
    public boolean isEnabled() { return Config.donutTotemWarning; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 310; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;

        // hat der spieler totem in offhand?
        boolean hasTotem = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
        if (hasTotem) return 0;

        // blinken
        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 200.0) * 0.5 + 0.5);
        int alpha = (int) (120 + pulse * 135);

        int accent = 0xFFFF2222;
        int borderCol = (accent & 0x00FFFFFF) | (alpha << 24);

        String text = "⚠ NO TOTEM IN OFFHAND ⚠";
        int tw = mc.textRenderer.getWidth(text);
        int width = tw + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + width, y + 1, borderCol);
        context.fill(x, y + height - 1, x + width, y + height, borderCol);
        context.fill(x, y, x + 1, y + height, borderCol);
        context.fill(x + width - 1, y, x + width, y + height, borderCol);

        int textCol = 0xFFFFFF | (alpha << 24);
        context.drawText(mc.textRenderer, text, x + PADDING, y + PADDING, textCol, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("⚠ NO TOTEM IN OFFHAND ⚠") + PADDING * 2;
    }
}