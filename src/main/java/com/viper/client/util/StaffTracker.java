package com.viper.client.util;

import java.util.ArrayList;
import java.util.List;

public class StaffTracker {

    private static final List<String> staffOnline = new ArrayList<>();
    private static long lastUpdate = 0;

    public static void update(List<String> names) {
        staffOnline.clear();
        if (names != null) {
            for (String name : names) {
                if (isStaff(name)) {
                    staffOnline.add(name);
                }
            }
        }
        lastUpdate = System.currentTimeMillis();
    }

    public static List<String> getStaffOnline() {
        // wenn älter als 10 sekunden → liste leeren (in case von disconnect)
        if (System.currentTimeMillis() - lastUpdate > 10000) {
            staffOnline.clear();
        }
        return new ArrayList<>(staffOnline);
    }

    public static boolean isStaff(String name) {
        if (name == null) return false;
        String n = name.toLowerCase();
        // DonutSMP typische staff-previews im tab:
        // "Admin John", "Mod X", "Helper Y", "Owner Z"
        return n.contains("admin") || n.contains("mod") || n.contains("helper") || n.contains("owner") || n.contains("staff");
    }
}