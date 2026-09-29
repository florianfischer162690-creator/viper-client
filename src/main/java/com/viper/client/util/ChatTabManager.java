package com.viper.client.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ChatTabManager {

    private static final int MAX_PER_TAB = 100;
    private static final Deque<String> party = new ArrayDeque<>();
    private static final Deque<String> guild = new ArrayDeque<>();
    private static final Deque<String> whisper = new ArrayDeque<>();
    private static final Deque<String> publicTab = new ArrayDeque<>();

    public static void archive(String message) {
        if (message == null) return;
        if (message.contains("[Party]") || message.toLowerCase().contains("party >")) {
            push(party, message);
        } else if (message.contains("[Guild]") || message.contains("[Clan]")) {
            push(guild, message);
        } else if (message.contains("whispers") || message.contains("[MSG]")) {
            push(whisper, message);
        } else {
            push(publicTab, message);
        }
    }

    public static List<String> get(String tab) {
        if (tab == null) return new ArrayList<>();
        switch (tab) {
            case "party": return new ArrayList<>(party);
            case "guild": return new ArrayList<>(guild);
            case "whisper": return new ArrayList<>(whisper);
            case "public": return new ArrayList<>(publicTab);
            default: return new ArrayList<>();
        }
    }

    public static void clearAll() {
        party.clear();
        guild.clear();
        whisper.clear();
        publicTab.clear();
    }

    private static void push(Deque<String> deque, String msg) {
        deque.addLast(msg);
        while (deque.size() > MAX_PER_TAB) deque.pollFirst();
    }
}