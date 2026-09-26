package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.AttackTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ComboElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_GOOD  = 0xFF23A55A;
    private static final int COLOR_HOT   = 0xFFFF3B30;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "combo"; }

    @Override
    public boolean isEnabled() { return Config.showCombo; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 112; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int combo = AttackTracker.getCombo();
        if (combo <= 0) return 0;
        int accent = Config.getAccent();

        String label = "COMBO";
        String value = String.valueOf(combo);

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);

        int valueColor;
        if (combo >= 8) valueColor = COLOR_HOT;
        else if (combo >= 4) valueColor = COLOR_GOOD;
        else valueColor = COLOR_VALUE;

        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("COMBO") + 6 + mc.textRenderer.getWidth("99") + PADDING * 2;
    }
}