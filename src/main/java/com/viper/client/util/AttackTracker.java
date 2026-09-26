package com.viper.client.util;

public class AttackTracker {

    private static float lastReach = 0f;
    private static long lastReachTime = 0L;

    private static int combo = 0;
    private static long lastComboTime = 0L;

    private static final long COMBO_TIMEOUT = 3000L; // 3 sekunden
    private static final long REACH_FADE = 4000L;    // 4 sekunden sichtbar

    public static void registerHit(double reach) {
        long now = System.currentTimeMillis();
        lastReach = (float) reach;
        lastReachTime = now;

        // combo
        if (now - lastComboTime < COMBO_TIMEOUT) {
            combo++;
        } else {
            combo = 1;
        }
        lastComboTime = now;
    }

    public static float getReach() {
        if (System.currentTimeMillis() - lastReachTime > REACH_FADE) return 0f;
        return lastReach;
    }

    public static int getCombo() {
        long now = System.currentTimeMillis();
        if (now - lastComboTime > COMBO_TIMEOUT) {
            combo = 0;
        }
        return combo;
    }

    public static boolean isComboActive() {
        return getCombo() > 0;
    }

    public static boolean isReachVisible() {
        return System.currentTimeMillis() - lastReachTime < REACH_FADE;
    }
}