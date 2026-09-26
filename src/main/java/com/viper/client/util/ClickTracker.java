package com.viper.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClickTracker {

    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();

    public static void registerLeft() {
        long now = System.currentTimeMillis();
        LEFT_CLICKS.addLast(now);
        prune(LEFT_CLICKS, now);
    }

    public static void registerRight() {
        long now = System.currentTimeMillis();
        RIGHT_CLICKS.addLast(now);
        prune(RIGHT_CLICKS, now);
    }

    public static int getLeftCps() {
        long now = System.currentTimeMillis();
        prune(LEFT_CLICKS, now);
        return LEFT_CLICKS.size();
    }

    public static int getRightCps() {
        long now = System.currentTimeMillis();
        prune(RIGHT_CLICKS, now);
        return RIGHT_CLICKS.size();
    }

    private static void prune(Deque<Long> clicks, long now) {
        // nur klicks der letzten 1000 ms behalten
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) {
            clicks.pollFirst();
        }
    }
}