package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class TntCountdownElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int COLOR_WARN  = 0xFFF1C40F;
    private static final int COLOR_BAD   = 0xFFED4245;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "tntcountdown"; }

    @Override
    public boolean isEnabled() { return Config.showTntCountdown; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 546; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null || mc.world == null) return 0;
        int accent = Config.getAccent();

        Box box = mc.player.getBoundingBox().expand(32.0);
        List<TntEntity> tnts = mc.world.getEntitiesByClass(TntEntity.class, box, Entity::isAlive);

        if (tnts.isEmpty()) return 0;

        TntEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (TntEntity t : tnts) {
            double d = t.squaredDistanceTo(mc.player);
            if (d < closestDist) { closestDist = d; closest = t; }
        }
        if (closest == null) return 0;

        int fuse = closest.getFuse();
        float seconds = fuse / 20.0f;
        double dist = Math.sqrt(closestDist);

        String label = "TNT";
        String value = String.format("%.1fs · %.0fm", seconds, dist);

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2;

        context.fill(x, y, x + width, y + height, COLOR_BG);

        int barColor;
        if (fuse <= 10) barColor = COLOR_BAD;
        else if (fuse <= 30) barColor = COLOR_WARN;
        else barColor = accent;

        context.fill(x, y, x + 2, y + height, barColor);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, barColor, true);

        int valueColor = fuse <= 10 ? COLOR_BAD : (fuse <= 30 ? COLOR_WARN : COLOR_VALUE);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, valueColor, true);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("TNT") + 6 + mc.textRenderer.getWidth("9.9s · 99m") + PADDING * 2;
    }
}