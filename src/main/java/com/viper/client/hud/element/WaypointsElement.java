package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.Waypoints;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class WaypointsElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int PADDING = 3;

    @Override
    public String getId() { return "waypoints"; }

    @Override
    public boolean isEnabled() { return Config.showWaypoints; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 292; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;

        int accent = Config.getAccent();

        if (Config.waypoints.isEmpty()) {
            String label = "WP";
            String value = "no waypoints — /wp set <name>";
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

        int px = (int) mc.player.getX();
        int py = (int) mc.player.getY();
        int pz = (int) mc.player.getZ();
        String dim = mc.world != null ? mc.world.getRegistryKey().getValue().toString() : "";

        Config.WaypointData closest = Waypoints.findClosest(px, py, pz, dim);
        if (closest == null) {
            closest = Waypoints.findClosestAny(px, py, pz);
        }
        if (closest == null) return 0;

        double dx = closest.x - px;
        double dy = closest.y - py;
        double dz = closest.z - pz;
        int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
        String dir = Waypoints.getDirection(px, pz, closest.x, closest.z);

        String label = "WP";
        String value = closest.name + " " + dir + " " + dist + "m";

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
        return mc.textRenderer.getWidth("WP") + 6 + mc.textRenderer.getWidth("this is a long waypoint name N 99999m") + PADDING * 2;
    }
}