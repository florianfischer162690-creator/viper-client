package com.viper.client.hud.element;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import com.viper.client.util.AncientDebrisScanner;
import com.viper.client.util.Titles;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class AncientDebrisEspRenderer {

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            try {
                render(context);
            } catch (Throwable t) {
                ViperClient.LOGGER.error("[DebrisESP] render failed", t);
            }
        });
    }

    private static void render(WorldRenderContext context) {
        if (!Config.ancientDebrisEsp) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        String username = mc.player.getName().getString();
        if (!Titles.isDevOrOwner(username)) return;

        var found = AncientDebrisScanner.getFound();
        if (found.isEmpty()) return;

        int color = Config.ancientDebrisColor;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 1.0f;

        MatrixStack matrices = context.matrices();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) return;

        double camX = mc.player.getX();
        double camY = mc.player.getEyeY();
        double camZ = mc.player.getZ();

        RenderLayer layer = null;
        try {
            layer = (RenderLayer) RenderLayers.class.getField("LINES").get(null);
        } catch (Throwable ignored) {}
        if (layer == null) {
            try {
                layer = (RenderLayer) RenderLayers.class.getField("DEBUG_LINES").get(null);
            } catch (Throwable ignored) {}
        }
        if (layer == null) return;

        VertexConsumer lines = consumers.getBuffer(layer);

        for (AncientDebrisScanner.DebrisPos dp : found) {
            double x = dp.x - camX;
            double y = dp.y - camY;
            double z = dp.z - camZ;

            drawBoxLines(matrices, lines, x, y, z, r, g, b, a);
        }
    }

    private static void drawBoxLines(MatrixStack matrices, VertexConsumer buf,
                                      double x, double y, double z,
                                      float r, float g, float b, float a) {
        var m = matrices.peek().getPositionMatrix();

        float x1 = (float) (x - 0.005);
        float y1 = (float) (y - 0.005);
        float z1 = (float) (z - 0.005);
        float x2 = (float) (x + 1.005);
        float y2 = (float) (y + 1.005);
        float z2 = (float) (z + 1.005);

        // unten (y1)
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x2, y1, z1).color(r, g, b, a);

        buf.vertex(m, x2, y1, z1).color(r, g, b, a);
        buf.vertex(m, x2, y1, z2).color(r, g, b, a);

        buf.vertex(m, x2, y1, z2).color(r, g, b, a);
        buf.vertex(m, x1, y1, z2).color(r, g, b, a);

        buf.vertex(m, x1, y1, z2).color(r, g, b, a);
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);

        // oben (y2)
        buf.vertex(m, x1, y2, z1).color(r, g, b, a);
        buf.vertex(m, x2, y2, z1).color(r, g, b, a);

        buf.vertex(m, x2, y2, z1).color(r, g, b, a);
        buf.vertex(m, x2, y2, z2).color(r, g, b, a);

        buf.vertex(m, x2, y2, z2).color(r, g, b, a);
        buf.vertex(m, x1, y2, z2).color(r, g, b, a);

        buf.vertex(m, x1, y2, z2).color(r, g, b, a);
        buf.vertex(m, x1, y2, z1).color(r, g, b, a);

        // vertikale
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x1, y2, z1).color(r, g, b, a);

        buf.vertex(m, x2, y1, z1).color(r, g, b, a);
        buf.vertex(m, x2, y2, z1).color(r, g, b, a);

        buf.vertex(m, x2, y1, z2).color(r, g, b, a);
        buf.vertex(m, x2, y2, z2).color(r, g, b, a);

        buf.vertex(m, x1, y1, z2).color(r, g, b, a);
        buf.vertex(m, x1, y2, z2).color(r, g, b, a);
    }
}