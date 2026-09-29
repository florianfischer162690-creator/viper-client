package com.viper.client.util;

import java.util.HashMap;
import java.util.Map;

public class EchestViewer {

    /** spielername -> echest-inhalt als text-zusammenfassung */
    private static final Map<String, String> echestData = new HashMap<>();
    private static final Map<String, Long> lastUpdate = new HashMap<>();
    private static final long FADE = 5 * 60 * 1000L; // 5 min

    public static void setContents(String player, String summary) {
        if (player == null || player.isEmpty()) return;
        if (summary == null) summary = "";
        echestData.put(player.toLowerCase(), summary);
        lastUpdate.put(player.toLowerCase(), System.currentTimeMillis());
    }

    public static String getContents(String player) {
        if (player == null) return null;
        String key = player.toLowerCase();
        Long ts = lastUpdate.get(key);
        if (ts == null) return null;
        if (System.currentTimeMillis() - ts > FADE) {
            echestData.remove(key);
            lastUpdate.remove(key);
            return null;
        }
        return echestData.get(key);
    }

    public static boolean has(String player) {
        return getContents(player) != null;
    }

    public static void clear() {
        echestData.clear();
        lastUpdate.clear();
    }
}