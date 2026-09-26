package com.viper.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viper.client.ViperClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    // HUD ELEMENTS
    public static boolean hudEnabled = true;
    public static boolean showFps = true;
    public static boolean showCps = true;
    public static boolean showCoords = true;
    public static boolean showPing = true;
    public static boolean showArmor = true;
    public static boolean showPotion = true;
    public static boolean showKeystrokes = true;
    public static boolean showReach = true;
    public static boolean showCombo = true;

    // PVP
    public static boolean toggleSprint = true;
    public static boolean showCustomCrosshair = true;
    public static boolean showTargetIndicator = true;

    // HUD POSITION
    public static int hudX = 4;
    public static int hudY = 4;
    public static float hudScale = 1.0f;

    public static Map<String, ElementPos> elementPositions = new HashMap<>();

    public static class ElementPos {
        public int x;
        public int y;
        public float scale = 1.0f;

        public ElementPos() {}
        public ElementPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static ElementPos getPos(String id, int defaultX, int defaultY) {
        return elementPositions.computeIfAbsent(id, k -> new ElementPos(defaultX, defaultY));
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;

    public static void load() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("viper.json");
        if (!Files.exists(configPath)) {
            ViperClient.LOGGER.info("[Viper] no config found, using defaults");
            save();
            return;
        }
        try {
            String json = Files.readString(configPath);
            ConfigData data = GSON.fromJson(json, ConfigData.class);
            if (data != null) applyFrom(data);
            ViperClient.LOGGER.info("[Viper] config loaded");
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Viper] failed to load config", e);
        }
    }

    public static void save() {
        if (configPath == null) {
            configPath = FabricLoader.getInstance().getConfigDir().resolve("viper.json");
        }
        try {
            ConfigData data = toData();
            Files.writeString(configPath, GSON.toJson(data));
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Viper] failed to save config", e);
        }
    }

    private static void applyFrom(ConfigData d) {
        hudEnabled = d.hudEnabled;
        showFps = d.showFps;
        showCps = d.showCps;
        showCoords = d.showCoords;
        showPing = d.showPing;
        showArmor = d.showArmor;
        showPotion = d.showPotion;
        showKeystrokes = d.showKeystrokes;
        showReach = d.showReach;
        showCombo = d.showCombo;
        toggleSprint = d.toggleSprint;
        showCustomCrosshair = d.showCustomCrosshair;
        showTargetIndicator = d.showTargetIndicator;
        hudX = d.hudX;
        hudY = d.hudY;
        hudScale = d.hudScale;
        elementPositions = d.elementPositions != null ? d.elementPositions : new HashMap<>();
    }

    private static ConfigData toData() {
        ConfigData d = new ConfigData();
        d.hudEnabled = hudEnabled;
        d.showFps = showFps;
        d.showCps = showCps;
        d.showCoords = showCoords;
        d.showPing = showPing;
        d.showArmor = showArmor;
        d.showPotion = showPotion;
        d.showKeystrokes = showKeystrokes;
        d.showReach = showReach;
        d.showCombo = showCombo;
        d.toggleSprint = toggleSprint;
        d.showCustomCrosshair = showCustomCrosshair;
        d.showTargetIndicator = showTargetIndicator;
        d.hudX = hudX;
        d.hudY = hudY;
        d.hudScale = hudScale;
        d.elementPositions = elementPositions;
        return d;
    }

    private static class ConfigData {
        boolean hudEnabled = true;
        boolean showFps = true;
        boolean showCps = true;
        boolean showCoords = true;
        boolean showPing = true;
        boolean showArmor = true;
        boolean showPotion = true;
        boolean showKeystrokes = true;
        boolean showReach = true;
        boolean showCombo = true;
        boolean toggleSprint = true;
        boolean showCustomCrosshair = true;
        boolean showTargetIndicator = true;
        int hudX = 4;
        int hudY = 4;
        float hudScale = 1.0f;
        Map<String, ElementPos> elementPositions = new HashMap<>();
    }
}