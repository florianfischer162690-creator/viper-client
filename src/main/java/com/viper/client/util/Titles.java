package com.viper.client.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Titles {

    /** DEV-USERS → dürfen "VIPER DEV" wählen */
    private static final Set<String> DEV_USERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "florianfischer",
            "bloodywinterd"
    ));

    /** BETA-TESTER → dürfen "VIPER BETA TESTER" + "VIPER ALPHA" wählen */
    private static final Set<String> BETA_TESTERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "bloodywinterd",
            "Test1",
            "Test2"
    ));

    /** STAFF-USERS → dürfen "VIPER STAFF" wählen */
    private static final Set<String> STAFF_USERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "bloodywinterd"
    ));

    /** alle titel: id, name, farbe, required-role (null = für alle) */
    public static final Title[] ALL_TITLES = {
            new Title("none",            "",                   0xFFFFFFFF, null),
            new Title("viper",           "VIPER",              0xFFA855F7, null),
            new Title("viper_pro",       "VIPER PRO",          0xFFC084FC, null),
            new Title("viper_legend",    "VIPER LEGEND",       0xFFFACC15, null),
            new Title("viper_og",        "VIPER OG",           0xFFFF9E3D, null),
            new Title("viper_supporter", "VIPER SUPPORTER",    0xFF23A55A, null),
            new Title("pvp_god",         "PVP GOD",            0xFFFF3B30, null),
            new Title("sweat",           "SWEAT",              0xFF3498DB, null),
            new Title("king",            "KING",               0xFFFFC107, null),
            new Title("legend",          "LEGEND",             0xFFFF69B4, null),
            new Title("viper_alpha",     "VIPER ALPHA",        0xFF00FFFF, "alpha"),
            new Title("viper_beta",      "VIPER BETA TESTER",  0xFF7C3AED, "beta"),
            new Title("viper_staff",     "VIPER STAFF",        0xFF2ECC71, "staff"),
            new Title("viper_dev",       "VIPER DEV",          0xFFFF0033, "dev"),
    };

    public static class Title {
        public final String id;
        public final String name;
        public final int color;
        public final String requiredRole;

        public Title(String id, String name, int color, String requiredRole) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.requiredRole = requiredRole;
        }

        public boolean isUnlockedFor(String username) {
            if (requiredRole == null) return true;
            if (username == null) return false;
            switch (requiredRole) {
                case "dev": return DEV_USERS.contains(username);
                case "beta": return BETA_TESTERS.contains(username);
                case "staff": return STAFF_USERS.contains(username);
                case "alpha": return BETA_TESTERS.contains(username) || DEV_USERS.contains(username);
            }
            return false;
        }
    }

    public static Title getById(String id) {
        for (Title t : ALL_TITLES) {
            if (t.id.equals(id)) return t;
        }
        return ALL_TITLES[0];
    }

    public static String getDisplayText(String id) {
        Title t = getById(id);
        if (t.name.isEmpty()) return "";
        return "[" + t.name + "]";
    }

    public static int getColor(String id) {
        return getById(id).color;
    }
}