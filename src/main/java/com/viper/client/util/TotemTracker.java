package com.viper.client.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TotemTracker {

    /** pops pro spieler-uuid */
    private static final Map<UUID, Integer> pops = new HashMap<>();
    /** letzter pop-zeitpunkt pro spieler-uuid (für ablauf) */
    private static final Map<UUID, Long> lastPop = new HashMap<>();

    /** pops laufen nach X ms aus (nur die anzeige) */
    private static final long FADE_TIME = 30000L; // 30s

    public static void registerPop(UUID uuid) {
        if (uuid == null) return;
        long now = System.currentTimeMillis();
        pops.merge(uuid, 1, Integer::sum);
        lastPop.put(uuid, now);
    }

    public static int getPops(UUID uuid) {
        if (uuid == null) return 0;
        Long last = lastPop.get(uuid);
        if (last == null) return 0;
        if (System.currentTimeMillis() - last > FADE_TIME) {
            // zu alt — reset
            pops.remove(uuid);
            lastPop.remove(uuid);
            return 0;
        }
        return pops.getOrDefault(uuid, 0);
    }

    public static void reset(UUID uuid) {
        pops.remove(uuid);
        lastPop.remove(uuid);
    }

    public static void resetAll() {
        pops.clear();
        lastPop.clear();
    }
}