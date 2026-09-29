package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CombatTimerElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_HOT   = 0xFFEF4444;
    private static final int COLOR_WARM  = 0xFFF1C40F;
    private static final int PADDING = 3;

    private static long lastCombat = 0L;
    private static final long COMBAT_DURATION = 15000L;

    public static void registerCombat() {
        lastCombat = System.currentTimeMillis();
    }

    @Override
    public String getId() { return "combattimer"; }

    @Override
    public boolean isEnabled() { return Config.showCombatTimer; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 600; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        long elapsed = System.currentTimeMillis() - lastCombat;
        if (lastCombat <= 0 || elapsed > COMBAT_DURATION) return 0;
        if (!com.viper.client.util.ServerTabs.showHugoTab()) return 0;

        int accent = Config.getAccent();

        long remaining = COMBAT_DURATION - elapsed;
        int seconds = (int) (remaining / 1000);
        int tenth = (int) ((remaining % 1000) / 100);

        String label = "FIGHT";
        String value = seconds + "." + tenth + "s";

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        int barColor;
        if (remaining > 10000) barColor = COLOR_HOT;
        else if (remaining > 5000) barColor = COLOR_WARM;
        else barColor = accent;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, barColor);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, barColor, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_VALUE, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("FIGHT") + 6 + mc.textRenderer.getWidth("99.9s") + PADDING * 2;
    }
}