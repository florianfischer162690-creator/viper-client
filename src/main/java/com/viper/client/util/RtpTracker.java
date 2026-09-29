package com.viper.client.util;

public class RtpTracker {

    private static long lastRtp = 0L;
    private static long cooldownMs = 0L;

    public static void registerRtp(long cooldownMs) {
        lastRtp = System.currentTimeMillis();
        RtpTracker.cooldownMs = cooldownMs;
    }

    public static long getRemainingMs() {
        if (lastRtp <= 0) return 0L;
        long elapsed = System.currentTimeMillis() - lastRtp;
        long remaining = cooldownMs - elapsed;
        return Math.max(0L, remaining);
    }

    public static boolean isOnCooldown() {
        return getRemainingMs() > 0L;
    }

    public static void reset() {
        lastRtp = 0L;
        cooldownMs = 0L;
    }
}