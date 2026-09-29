package com.viper.client.util;

import java.util.HashMap;
import java.util.Map;

public class BalanceTracker {

    private static final Map<String, Long> balances = new HashMap<>();
    private static final long FADE = 30000L;
    private static final Map<String, Long> lastUpdate = new HashMap<>();

    public static void setBalance(String player, long amount) {
        if (player == null || player.isEmpty()) return;
        balances.put(player.toLowerCase(), amount);
        lastUpdate.put(player.toLowerCase(), System.currentTimeMillis());
    }

    public static long getBalance(String player) {
        if (player == null) return -1;
        String key = player.toLowerCase();
        Long ts = lastUpdate.get(key);
        if (ts == null) return -1;
        if (System.currentTimeMillis() - ts > FADE) {
            balances.remove(key);
            lastUpdate.remove(key);
            return -1;
        }
        Long b = balances.get(key);
        return b == null ? -1 : b;
    }

    public static boolean has(String player) {
        return getBalance(player) >= 0;
    }

    public static void clear() {
        balances.clear();
        lastUpdate.clear();
    }
}