package com.viper.client.util;

public class RegionTracker {

    private static String currentRegion = "";
    private static long lastUpdate = 0L;
    private static final long FADE_TIME = 10000L;

    public static void setRegion(String region) {
        if (region == null || region.isEmpty()) return;
        currentRegion = region;
        lastUpdate = System.currentTimeMillis();
    }

    public static String getRegion() {
        if (System.currentTimeMillis() - lastUpdate > FADE_TIME) {
            currentRegion = "";
        }
        return currentRegion;
    }

    public static boolean hasRegion() {
        return !getRegion().isEmpty();
    }
}