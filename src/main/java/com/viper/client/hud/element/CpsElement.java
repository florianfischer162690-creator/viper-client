package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.ClickTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CpsElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_GOOD  = 0xFF23A55A;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "cps"; }

    @Override
    public boolean isEnabled() { return Config.showCps; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 40; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int accent = Config.getAccent();
        int left = ClickTracker.getLeftCps();
        int right = ClickTracker.getRightCps();

        String label = "CPS";
        String value = left + " | " + right;

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);

        int valueColor = left > 0 ? COLOR_GOOD : COLOR_VALUE;
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("CPS") + 6 + mc.textRenderer.getWidth("99 | 99") + PADDING * 2;
    }
}