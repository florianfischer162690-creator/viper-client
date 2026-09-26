package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class WatermarkElement extends HudElement {

    private static final int COLOR_ACCENT  = 0xFFA855F7;
    private static final int COLOR_ACCENT2 = 0xFFC084FC;
    private static final int COLOR_BG      = 0x66000000;
    private static final int PADDING = 4;

    @Override
    public String getId() { return "watermark"; }

    @Override
    public boolean isEnabled() {
        return Config.showWatermark;
    }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 4; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        String left = "VIPER";
        String right = "V1";
        int lw = mc.textRenderer.getWidth(left);
        int rw = mc.textRenderer.getWidth(right);

        int gap = 4;
        int width = lw + gap + rw + PADDING * 2 + 4;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        // hintergrund
        context.fill(x, y, x + width, y + height, COLOR_BG);
        // lila akzent-streifen links
        context.fill(x, y, x + 2, y + height, COLOR_ACCENT);

        // "VIPER" bold
        context.drawText(mc.textRenderer, "§l" + left, x + PADDING + 3, y + PADDING, COLOR_ACCENT, true);

        // "V1" normal
        context.drawText(mc.textRenderer, right, x + PADDING + 3 + lw + gap, y + PADDING, COLOR_ACCENT2, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("VIPER") + 4 + mc.textRenderer.getWidth("V1") + PADDING * 2 + 4;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return mc.textRenderer.fontHeight + PADDING * 2;
    }
}
