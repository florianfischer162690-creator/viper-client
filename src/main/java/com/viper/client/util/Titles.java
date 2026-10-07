package com.viper.client.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Titles {

    private static final Set<String> DEV_USERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "florianfischer",
            "bloodywinterd",
            "Dergamer18"
    ));

    private static final Set<String> BETA_TESTERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "bloodywinterd"
    ));

    private static final Set<String> STAFF_USERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "bloodywinterd"
    ));

    private static final Set<String> OWNER_USERS = new HashSet<>(Arrays.asList(
            "Vipez20",
            "Dergamer18"
    ));

    private static final Set<String> SUPPORTER_USERS = new HashSet<>(Arrays.asList(
            "Vipez20"
    ));

    public static final Title[] ALL_TITLES = {
            new Title("none",            "",                   0xFFFFFFFF, null),
            new Title("viper",           "VIPER",              0xFFA855F7, null),
            new Title("viper_pro",       "VIPER PRO",          0xFFC084FC, null),
            new Title("viper_legend",    "VIPER LEGEND",       0xFFFACC15, null),
            new Title("viper_supporter", "VIPER SUPPORTER",    0xFF23A55A, "supporter"),
            new Title("viper_alpha",     "VIPER ALPHA",        0xFF00FFFF, "alpha"),
            new Title("viper_beta",      "VIPER BETA TESTER",  0xFF7C3AED, "beta"),
            new Title("viper_staff",     "VIPER STAFF",        0xFF2ECC71, "staff"),
            new Title("viper_dev",       "VIPER DEV",          0xFFFF0033, "dev"),
            new Title("viper_owner",     "VIPER OWNER",        0xFFFFD700, "owner"),
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
                case "owner": return OWNER_USERS.contains(username);
                case "supporter": return SUPPORTER_USERS.contains(username);
            }
            return false;
        }
    }

    public static boolean isDevOrOwner(String username) {
        if (username == null) return false;
        return DEV_USERS.contains(username) || OWNER_USERS.contains(username);
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