package com.viper.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viper.client.ViperClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Config {
    // HUD
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
    public static boolean showWatermark = true;
    public static boolean showDirection = true;
    public static boolean showSpeed = true;

    // PVP
    public static boolean toggleSprint = true;
    public static boolean toggleSneak = false;
    public static boolean showCustomCrosshair = true;
    public static boolean showTargetIndicator = true;
    public static boolean showHealthIndicator = true;
    public static boolean showHitMarker = true;

    // CROSSHAIR
    public static String crosshairPreset = "classic";

    // THEME
    public static String theme = "viper";

    // FARBEN
    public static boolean useCustomCrosshairColor = false;
    public static boolean useCustomTargetColor = false;
    public static boolean useCustomHealthColor = false;
    public static boolean useCustomHitMarkerColor = false;

    public static int crosshairColor = 0xFFA855F7;
    public static int targetColor = 0xFFFF3B30;
    public static int healthIndicatorColor = 0xFFFF3B30;
    public static int hitMarkerColor = 0xFFFF3B30;

    // MISC
    public static boolean fullbright = false;
    public static boolean noFog = false;
    public static boolean lowHealthWarning = true;
    public static boolean fpsBoost = false;
    public static int fpsPrevRenderDist = 8;
    public static int fpsPrevSimDist = 12;
    public static int fpsPrevEntityDist = 100;

    // ZOOM
    public static boolean zoomEnabled = true;
    public static int zoomFov = 30;
    public static int defaultFov = 70;

    // CHAT
    public static boolean chatTimestamps = true;

    // APPLESKIN
    public static boolean showAppleskin = true;

    // SHULKER PREVIEW
    public static boolean showShulkerPreview = true;

    // INVENTORY HUD
    public static boolean showInventoryHud = true;

    // TITLE SYSTEM
    public static boolean showTitle = true;
    public static String currentTitle = "viper";

    // CHAT MACROS
    public static String[] macroTexts = new String[]{"", "", "", "", "", ""};
    public static boolean[] macroEnabled = new boolean[]{true, true, true, true, true, true};

    // CRYSTAL OPTIMIZER
    public static boolean crystalOptimizer = true;

    // ANCHOR OPTIMIZER
    public static boolean anchorOptimizer = true;

    // BLOCK BREAK PROGRESS
    public static boolean showBlockBreakProgress = true;

    // SATURATION BAR
    public static boolean showSaturationBar = true;

    // ABSORPTION HEART INDICATOR
    public static boolean showAbsorptionHearts = true;

    // SHIELD STATUS
    public static boolean showShieldStatus = true;

    // ENCHANT GLINT COLOR
    public static boolean customGlintColor = true;

    // FREECAM
    public static boolean freecamEnabled = false;
    public static double freecamX = 0;
    public static double freecamY = 0;
    public static double freecamZ = 0;
    public static float freecamYaw = 0;
    public static float freecamPitch = 0;

    // KEYBINDS
    public static Map<String, Integer> keybinds = new HashMap<>();

    public static int getKeybind(String id) { return keybinds.getOrDefault(id, -1); }
    public static void setKeybind(String id, int key) {
        if (key <= 0) keybinds.remove(id);
        else keybinds.put(id, key);
    }

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
        public ElementPos(int x, int y) { this.x = x; this.y = y; }
    }

    public static ElementPos getPos(String id, int defaultX, int defaultY) {
        return elementPositions.computeIfAbsent(id, k -> new ElementPos(defaultX, defaultY));
    }

    /* ═══════════ THEME ═══════════ */

    private static final Map<String, int[]> THEME_COLORS = new HashMap<>();
    static {
        THEME_COLORS.put("viper",   new int[]{0xFFA855F7, 0xFFC084FC, 0xFF7c3aed});
        THEME_COLORS.put("blue",    new int[]{0xFF3B82F6, 0xFF60A5FA, 0xFF2563EB});
        THEME_COLORS.put("green",   new int[]{0xFF22C55E, 0xFF4ADE80, 0xFF16A34A});
        THEME_COLORS.put("red",     new int[]{0xFFEF4444, 0xFFF87171, 0xFFDC2626});
        THEME_COLORS.put("yellow",  new int[]{0xFFFACC15, 0xFFFDE047, 0xFFEAB308});
        THEME_COLORS.put("pink",    new int[]{0xFFEC4899, 0xFFF472B6, 0xFFDB2777});
        THEME_COLORS.put("white",   new int[]{0xFFE5E5E5, 0xFFF5F5F5, 0xFFA3A3A3});
        THEME_COLORS.put("vanilla", new int[]{0xFF7c7c7c, 0xFF9c9c9c, 0xFF545454});
    }

    public static int getAccent() {
        int[] t = THEME_COLORS.getOrDefault(theme, THEME_COLORS.get("viper"));
        return t[0];
    }
    public static int getAccentLight() {
        int[] t = THEME_COLORS.getOrDefault(theme, THEME_COLORS.get("viper"));
        return t[1];
    }
    public static int getAccentDark() {
        int[] t = THEME_COLORS.getOrDefault(theme, THEME_COLORS.get("viper"));
        return t[2];
    }

    public static int getAccentBg() {
        int a = getAccent();
        int r = ((a >> 16) & 0xFF) * 20 / 100;
        int g = ((a >> 8) & 0xFF) * 20 / 100;
        int b = (a & 0xFF) * 20 / 100;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static int getAccentHover() {
        return (getAccent() & 0x00FFFFFF) | 0x33000000;
    }

    public static int getEffectiveCrosshairColor() {
        return useCustomCrosshairColor ? crosshairColor : getAccent();
    }
    public static int getEffectiveTargetColor() {
        return useCustomTargetColor ? targetColor : 0xFFFF3B30;
    }
    public static int getEffectiveHealthColor() {
        return useCustomHealthColor ? healthIndicatorColor : 0xFF23A55A;
    }
    public static int getEffectiveHitMarkerColor() {
        return useCustomHitMarkerColor ? hitMarkerColor : getAccent();
    }

    /* ═══════════ EXPORT / IMPORT ═══════════ */

    public static String exportAsString() {
        try {
            ConfigData data = toData();
            String json = GSON.toJson(data);
            return "VIPER-CFG-V1:" + Base64.getEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            ViperClient.LOGGER.error("[Config] export failed", e);
            return null;
        }
    }

    public static boolean importFromString(String raw) {
        try {
            String input = raw.trim();
            if (input.startsWith("VIPER-CFG-V1:")) {
                input = input.substring("VIPER-CFG-V1:".length());
            }
            byte[] decoded = Base64.getDecoder().decode(input);
            String json = new String(decoded);
            ConfigData data = GSON.fromJson(json, ConfigData.class);
            if (data == null) return false;
            applyFrom(data);
            save();
            return true;
        } catch (Exception e) {
            ViperClient.LOGGER.error("[Config] import failed", e);
            return false;
        }
    }

    /* ═══════════ LOAD / SAVE ═══════════ */

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;

    public static void load() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("viper.json");
        if (!Files.exists(configPath)) { ViperClient.LOGGER.info("[Viper] no config found"); save(); return; }
        try {
            String json = Files.readString(configPath);
            ConfigData data = GSON.fromJson(json, ConfigData.class);
            if (data != null) applyFrom(data);
            ViperClient.LOGGER.info("[Viper] config loaded");
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Viper] load failed", e);
        }
    }

    public static void save() {
        if (configPath == null) configPath = FabricLoader.getInstance().getConfigDir().resolve("viper.json");
        try {
            Files.writeString(configPath, GSON.toJson(toData()));
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Viper] save failed", e);
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
        showWatermark = d.showWatermark;
        showDirection = d.showDirection;
        showSpeed = d.showSpeed;
        toggleSprint = d.toggleSprint;
        toggleSneak = d.toggleSneak;
        showCustomCrosshair = d.showCustomCrosshair;
        showTargetIndicator = d.showTargetIndicator;
        showHealthIndicator = d.showHealthIndicator;
        showHitMarker = d.showHitMarker;
        crosshairPreset = d.crosshairPreset != null ? d.crosshairPreset : "classic";
        theme = d.theme != null ? d.theme : "viper";
        useCustomCrosshairColor = d.useCustomCrosshairColor;
        useCustomTargetColor = d.useCustomTargetColor;
        useCustomHealthColor = d.useCustomHealthColor;
        useCustomHitMarkerColor = d.useCustomHitMarkerColor;
        crosshairColor = d.crosshairColor;
        targetColor = d.targetColor;
        healthIndicatorColor = d.healthIndicatorColor;
        hitMarkerColor = d.hitMarkerColor;
        fullbright = d.fullbright;
        noFog = d.noFog;
        lowHealthWarning = d.lowHealthWarning;
        fpsBoost = d.fpsBoost;
        fpsPrevRenderDist = d.fpsPrevRenderDist;
        fpsPrevSimDist = d.fpsPrevSimDist;
        fpsPrevEntityDist = d.fpsPrevEntityDist;
        zoomEnabled = d.zoomEnabled;
        zoomFov = d.zoomFov;
        defaultFov = d.defaultFov;
        chatTimestamps = d.chatTimestamps;
        showAppleskin = d.showAppleskin;
        showShulkerPreview = d.showShulkerPreview;
        showInventoryHud = d.showInventoryHud;
        showTitle = d.showTitle;
        currentTitle = d.currentTitle != null ? d.currentTitle : "viper";
        macroTexts = d.macroTexts != null && d.macroTexts.length == 6 ? d.macroTexts : new String[]{"", "", "", "", "", ""};
        macroEnabled = d.macroEnabled != null && d.macroEnabled.length == 6 ? d.macroEnabled : new boolean[]{true, true, true, true, true, true};
        crystalOptimizer = d.crystalOptimizer;
        anchorOptimizer = d.anchorOptimizer;
        showBlockBreakProgress = d.showBlockBreakProgress;
        showSaturationBar = d.showSaturationBar;
        showAbsorptionHearts = d.showAbsorptionHearts;
        showShieldStatus = d.showShieldStatus;
        customGlintColor = d.customGlintColor;
        freecamEnabled = d.freecamEnabled;
        freecamX = d.freecamX;
        freecamY = d.freecamY;
        freecamZ = d.freecamZ;
        freecamYaw = d.freecamYaw;
        freecamPitch = d.freecamPitch;
        hudX = d.hudX;
        hudY = d.hudY;
        hudScale = d.hudScale;
        elementPositions = d.elementPositions != null ? d.elementPositions : new HashMap<>();
        keybinds = d.keybinds != null ? d.keybinds : new HashMap<>();
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
        d.showWatermark = showWatermark;
        d.showDirection = showDirection;
        d.showSpeed = showSpeed;
        d.toggleSprint = toggleSprint;
        d.toggleSneak = toggleSneak;
        d.showCustomCrosshair = showCustomCrosshair;
        d.showTargetIndicator = showTargetIndicator;
        d.showHealthIndicator = showHealthIndicator;
        d.showHitMarker = showHitMarker;
        d.crosshairPreset = crosshairPreset;
        d.theme = theme;
        d.useCustomCrosshairColor = useCustomCrosshairColor;
        d.useCustomTargetColor = useCustomTargetColor;
        d.useCustomHealthColor = useCustomHealthColor;
        d.useCustomHitMarkerColor = useCustomHitMarkerColor;
        d.crosshairColor = crosshairColor;
        d.targetColor = targetColor;
        d.healthIndicatorColor = healthIndicatorColor;
        d.hitMarkerColor = hitMarkerColor;
        d.fullbright = fullbright;
        d.noFog = noFog;
        d.lowHealthWarning = lowHealthWarning;
        d.fpsBoost = fpsBoost;
        d.fpsPrevRenderDist = fpsPrevRenderDist;
        d.fpsPrevSimDist = fpsPrevSimDist;
        d.fpsPrevEntityDist = fpsPrevEntityDist;
        d.zoomEnabled = zoomEnabled;
        d.zoomFov = zoomFov;
        d.defaultFov = defaultFov;
        d.chatTimestamps = chatTimestamps;
        d.showAppleskin = showAppleskin;
        d.showShulkerPreview = showShulkerPreview;
        d.showInventoryHud = showInventoryHud;
        d.showTitle = showTitle;
        d.currentTitle = currentTitle;
        d.macroTexts = macroTexts;
        d.macroEnabled = macroEnabled;
        d.crystalOptimizer = crystalOptimizer;
        d.anchorOptimizer = anchorOptimizer;
        d.showBlockBreakProgress = showBlockBreakProgress;
        d.showSaturationBar = showSaturationBar;
        d.showAbsorptionHearts = showAbsorptionHearts;
        d.showShieldStatus = showShieldStatus;
        d.customGlintColor = customGlintColor;
        d.freecamEnabled = freecamEnabled;
        d.freecamX = freecamX;
        d.freecamY = freecamY;
        d.freecamZ = freecamZ;
        d.freecamYaw = freecamYaw;
        d.freecamPitch = freecamPitch;
        d.hudX = hudX;
        d.hudY = hudY;
        d.hudScale = hudScale;
        d.elementPositions = elementPositions;
        d.keybinds = keybinds;
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
        boolean showWatermark = true;
        boolean showDirection = true;
        boolean showSpeed = true;
        boolean toggleSprint = true;
        boolean toggleSneak = false;
        boolean showCustomCrosshair = true;
        boolean showTargetIndicator = true;
        boolean showHealthIndicator = true;
        boolean showHitMarker = true;
        String crosshairPreset = "classic";
        String theme = "viper";
        boolean useCustomCrosshairColor = false;
        boolean useCustomTargetColor = false;
        boolean useCustomHealthColor = false;
        boolean useCustomHitMarkerColor = false;
        int crosshairColor = 0xFFA855F7;
        int targetColor = 0xFFFF3B30;
        int healthIndicatorColor = 0xFFFF3B30;
        int hitMarkerColor = 0xFFFF3B30;
        boolean fullbright = false;
        boolean noFog = false;
        boolean lowHealthWarning = true;
        boolean fpsBoost = false;
        int fpsPrevRenderDist = 8;
        int fpsPrevSimDist = 12;
        int fpsPrevEntityDist = 100;
        boolean zoomEnabled = true;
        int zoomFov = 30;
        int defaultFov = 70;
        boolean chatTimestamps = true;
        boolean showAppleskin = true;
        boolean showShulkerPreview = true;
        boolean showInventoryHud = true;
        boolean showTitle = true;
        String currentTitle = "viper";
        String[] macroTexts = new String[]{"", "", "", "", "", ""};
        boolean[] macroEnabled = new boolean[]{true, true, true, true, true, true};
        boolean crystalOptimizer = true;
        boolean anchorOptimizer = true;
        boolean showBlockBreakProgress = true;
        boolean showSaturationBar = true;
        boolean showAbsorptionHearts = true;
        boolean showShieldStatus = true;
        boolean customGlintColor = true;
        boolean freecamEnabled = false;
        double freecamX = 0;
        double freecamY = 0;
        double freecamZ = 0;
        float freecamYaw = 0;
        float freecamPitch = 0;
        int hudX = 4;
        int hudY = 4;
        float hudScale = 1.0f;
        Map<String, ElementPos> elementPositions = new HashMap<>();
        Map<String, Integer> keybinds = new HashMap<>();
    }
}