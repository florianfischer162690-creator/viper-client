package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FpsElement extends HudElement {

    private static final int COLOR_LABEL = 0xFFA855F7;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_GOOD  = 0xFF23A55A;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BAD   = 0xFFED4245;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "fps"; }

    @Override
    public boolean isEnabled() { return Config.showFps; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 4; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        int fps = mc.getCurrentFps();
        String label = "FPS";
        String value = String.valueOf(fps);

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, COLOR_LABEL);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, COLOR_LABEL, true);

        int valueColor;
        if (fps >= 120) valueColor = COLOR_GOOD;
        else if (fps >= 60) valueColor = COLOR_VALUE;
        else if (fps >= 30) valueColor = COLOR_WARN;
        else valueColor = COLOR_BAD;

        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("FPS") + 6 + mc.textRenderer.getWidth("999") + PADDING * 2;
    }
}