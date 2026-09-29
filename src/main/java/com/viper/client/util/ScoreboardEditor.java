package com.viper.client.util;

import com.viper.client.config.Config;

public class ScoreboardEditor {

    /**
     * parst den custom-text: "Viper V1|Kills: 5|Rank: Owner"
     * -> split bei "|"
     */
    public static String[] getFakeLines() {
        String raw = Config.scoreboardFakeText;
        if (raw == null || raw.isEmpty()) return new String[0];
        String[] lines = raw.split("\\|");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        return lines;
    }

    public static boolean shouldHide() {
        return Config.hideScoreboard;
    }

    public static boolean shouldFake() {
        return Config.fakeScoreboard;
    }
}