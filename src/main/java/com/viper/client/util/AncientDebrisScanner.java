package com.viper.client.util;

import com.viper.client.config.Config;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class AncientDebrisScanner {

    public static class DebrisPos {
        public final int x, y, z;
        public DebrisPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final List<DebrisPos> FOUND = new ArrayList<>();
    private static long lastScan = 0L;
    private static final long SCAN_INTERVAL = 1000L;

    public static List<DebrisPos> getFound() {
        return FOUND;
    }

    public static void tick() {
        if (!Config.ancientDebrisEsp) {
            if (!FOUND.isEmpty()) FOUND.clear();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastScan < SCAN_INTERVAL) return;
        lastScan = now;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        String username = mc.player.getName().getString();
        if (!Titles.isDevOrOwner(username)) {
            if (!FOUND.isEmpty()) FOUND.clear();
            return;
        }

        scan(mc, mc.world);
    }

    private static void scan(MinecraftClient mc, ClientWorld world) {
        FOUND.clear();

        int radius = Config.ancientDebrisRadius;
        int px = (int) mc.player.getX();
        int py = (int) mc.player.getY();
        int pz = (int) mc.player.getZ();

        int chunkRadius = (radius >> 4) + 1;
        int playerChunkX = px >> 4;
        int playerChunkZ = pz >> 4;

        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                WorldChunk chunk = world.getChunk(playerChunkX + cx, playerChunkZ + cz);
                if (chunk == null) continue;
                scanChunk(chunk, px, py, pz, radius);
            }
        }
    }

    private static void scanChunk(WorldChunk chunk, int px, int py, int pz, int radius) {
        int radiusSq = radius * radius;
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();

        int minY = Math.max(chunk.getBottomY(), py - radius);
        int maxY = Math.min(chunk.getBottomY() + chunk.getHeight(), py + radius);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = baseX + x;
                int worldZ = baseZ + z;
                int dx = worldX - px;
                int dz = worldZ - pz;
                if (dx * dx + dz * dz > radiusSq) continue;

                for (int y = minY; y <= maxY; y++) {
                    try {
                        BlockPos pos = new BlockPos(worldX, y, worldZ);
                        if (chunk.getBlockState(pos).isOf(Blocks.ANCIENT_DEBRIS)) {
                            int dy = y - py;
                            if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                                FOUND.add(new DebrisPos(worldX, y, worldZ));
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
        }
    }
}