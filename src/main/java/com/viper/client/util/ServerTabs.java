package com.viper.client.util;

import net.minecraft.client.MinecraftClient;

public class ServerTabs {

    public static boolean isStaffTierUser() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;
        String username = mc.player.getName().getString();
        return Titles.isDevOrOwner(username);
    }

    public static boolean isOnHugo() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentServerEntry() == null) return false;
        String addr = mc.getCurrentServerEntry().address;
        if (addr == null) return false;
        addr = addr.toLowerCase();
        return addr.contains("hugo");
    }

    public static boolean isOnDonut() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentServerEntry() == null) return false;
        String addr = mc.getCurrentServerEntry().address;
        if (addr == null) return false;
        addr = addr.toLowerCase();
        return addr.contains("donut");
    }

    public static boolean showHugoTab() {
        if (isStaffTierUser()) return true;
        return isOnHugo();
    }

    public static boolean showDonutTab() {
        if (isStaffTierUser()) return true;
        return isOnDonut();
    }
}