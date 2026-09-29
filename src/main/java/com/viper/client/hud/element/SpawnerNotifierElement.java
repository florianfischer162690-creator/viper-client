package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import com.viper.client.util.ServerTabs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class SpawnerNotifierElement extends HudElement {

    private static final int COLOR_BG = 0xCC0a1a0a;
    private static final int PADDING = 4;

    private static BlockPos lastSpawner = null;
    private static long lastCheck = 0L;
    private static final long CHECK_INTERVAL = 500L;
    private static final int SCAN_RADIUS = 16;

    @Override
    public String getId() { return "spawnernotifier"; }

    @Override
    public boolean isEnabled() { return Config.donutSpawnerNotifier; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 618; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (!ServerTabs.showDonutTab()) return 0;
        if (mc.player == null || mc.world == null) return 0;

        long now = System.currentTimeMillis();
        if (now - lastCheck < CHECK_INTERVAL) {
            if (lastSpawner == null) return 0;
        } else {
            lastCheck = now;
            lastSpawner = findClosestSpawner(mc);
        }

        if (lastSpawner == null) return 0;

        double dx = lastSpawner.getX() + 0.5 - mc.player.getX();
        double dy = lastSpawner.getY() + 0.5 - mc.player.getY();
        double dz = lastSpawner.getZ() + 0.5 - mc.player.getZ();
        int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);

        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 300.0) * 0.5 + 0.5);
        int alpha = (int) (140 + pulse * 115);

        int accent = 0xFF44FF44;
        int borderCol = (accent & 0x00FFFFFF) | (alpha << 24);

        String text = "⚠ SPAWNER " + dist + "m";
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

    private BlockPos findClosestSpawner(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return null;

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; dy++) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                    BlockPos p = playerPos.add(dx, dy, dz);
                    try {
                        if (mc.world.getBlockState(p).isOf(Blocks.SPAWNER)) {
                            double d = playerPos.getSquaredDistance(p);
                            if (d < closestDist) {
                                closestDist = d;
                                closest = p;
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
        }
        return closest;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("⚠ SPAWNER 99m") + PADDING * 2;
    }
}