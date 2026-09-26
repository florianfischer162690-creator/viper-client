package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DirectionElement extends HudElement {

    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_BG    = 0x66000000;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "direction"; }

    @Override
    public boolean isEnabled() { return Config.showDirection; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 148; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();

        float yaw = mc.player.getYaw();
        // yaw normalisieren
        yaw = ((yaw % 360) + 360) % 360;

        String dir;
        if (yaw >= 315 || yaw < 45) dir = "S";
        else if (yaw < 135) dir = "W";
        else if (yaw < 225) dir = "N";
        else dir = "E";

        String label = "DIR";
        String value = dir;

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_VALUE, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("DIR") + 6 + mc.textRenderer.getWidth("S") + PADDING * 2;
    }
}