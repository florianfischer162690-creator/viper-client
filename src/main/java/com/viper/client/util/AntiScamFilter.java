package com.viper.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AntiScamFilter {

    private static final List<String> SCAM_PATTERNS = new ArrayList<>();

    static {
        // typische scam-phrasen auf deutschen servern
        SCAM_PATTERNS.add("du hast gewonnen");
        SCAM_PATTERNS.add("kostenlos");
        SCAM_PATTERNS.add("free rank");
        SCAM_PATTERNS.add("free money");
        SCAM_PATTERNS.add("gratis rang");
        SCAM_PATTERNS.add("gratis geld");
        SCAM_PATTERNS.add("klick hier");
        SCAM_PATTERNS.add("click here");
        SCAM_PATTERNS.add("bit.ly");
        SCAM_PATTERNS.add("youtube.com/");
        SCAM_PATTERNS.add("youtu.be");
        SCAM_PATTERNS.add("discord.gg/");
        SCAM_PATTERNS.add("trade me");
        SCAM_PATTERNS.add("tausche gegen");
        SCAM_PATTERNS.add("verdoppelt");
        SCAM_PATTERNS.add("dupe");
        SCAM_PATTERNS.add("trust trade");
        SCAM_PATTERNS.add("free stuff");
        SCAM_PATTERNS.add("gratis zeug");
        SCAM_PATTERNS.add("account verified");
        SCAM_PATTERNS.add("verify now");
        SCAM_PATTERNS.add("op item");
        SCAM_PATTERNS.add("op items");
        SCAM_PATTERNS.add("hacked client");
        SCAM_PATTERNS.add("free cape");
        SCAM_PATTERNS.add("free cosmetics");
        SCAM_PATTERNS.add("minecraft.net/gift");
        SCAM_PATTERNS.add("/give");
        SCAM_PATTERNS.add("cheat engine");
        SCAM_PATTERNS.add("server boost");
    }

    public static boolean isScam(String message) {
        if (message == null || message.isEmpty()) return false;
        String lower = message.toLowerCase(Locale.ROOT);

        for (String pattern : SCAM_PATTERNS) {
            if (lower.contains(pattern)) return true;
        }
        return false;
    }

    public static List<String> getPatterns() {
        return new ArrayList<>(SCAM_PATTERNS);
    }
}