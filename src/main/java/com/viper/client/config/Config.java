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

    public static boolean toggleSprint = true;
    public static boolean toggleSneak = false;
    public static boolean showCustomCrosshair = true;
    public static boolean showTargetIndicator = true;
    public static boolean showHealthIndicator = true;
    public static boolean showHitMarker = true;

    public static String crosshairPreset = "classic";
    public static String theme = "viper";

    public static boolean useCustomCrosshairColor = false;
    public static boolean useCustomTargetColor = false;
    public static boolean useCustomHealthColor = false;
    public static boolean useCustomHitMarkerColor = false;

    public static int crosshairColor = 0xFFA855F7;
    public static int targetColor = 0xFFFF3B30;
    public static int healthIndicatorColor = 0xFFFF3B30;
    public static int hitMarkerColor = 0xFFFF3B30;

    public static boolean fullbright = false;
    public static boolean noFog = false;
    public static boolean lowHealthWarning = true;
    public static boolean fpsBoost = false;
    public static int fpsPrevRenderDist = 8;
    public static int fpsPrevSimDist = 12;
    public static int fpsPrevEntityDist = 100;

    public static boolean zoomEnabled = true;
    public static int zoomFov = 30;
    public static int defaultFov = 70;

    public static boolean chatTimestamps = true;
    public static boolean showAppleskin = true;
    public static boolean showShulkerPreview = true;
    public static boolean showInventoryHud = true;

    public static boolean showTitle = true;
    public static String currentTitle = "viper";

    public static String[] macroTexts = new String[]{"", "", "", "", "", ""};
    public static boolean[] macroEnabled = new boolean[]{true, true, true, true, true, true};

    public static boolean crystalOptimizer = true;
    public static boolean anchorOptimizer = true;
    public static boolean showBlockBreakProgress = true;
    public static boolean showSaturationBar = true;
    public static boolean showAbsorptionHearts = true;
    public static boolean showShieldStatus = true;
    public static boolean customGlintColor = true;

    public static boolean freecamEnabled = false;
    public static double freecamX = 0;
    public static double freecamY = 0;
    public static double freecamZ = 0;
    public static float freecamYaw = 0;
    public static float freecamPitch = 0;

    // HUGOSMP
    public static boolean hugoAutoInvsee = true;
    public static boolean antiBlockRotation = true;

    // DONUTSMP
    public static boolean donutAdminHud = true;
    public static boolean donutCoordSnapper = true;
    public static boolean donutRegionMap = true;
    public static boolean donutBalanceTracker = true;
    public static boolean donutEchestViewer = true;
    public static boolean donutTotemWarning = true;
    public static boolean donutChestFilter = true;
    public static boolean donutAntiScam = true;
    public static boolean donutSpawnerNotifier = true;

    // WAYPOINTS
    public static boolean showWaypoints = true;
    public static Map<String, WaypointData> waypoints = new HashMap<>();

    // STOPWATCH
    public static boolean showStopwatch = true;

    // CLOCK
    public static boolean showClock = true;

    // DAY COUNTER
    public static boolean showDayCounter = true;

    // PLAYTIME
    public static boolean showPlaytime = true;

    // MEMORY
    public static boolean showMemory = true;

    // SERVER ADDRESS
    public static boolean showServerAddress = true;

    // PING GRAPH
    public static boolean showPingGraph = true;

    // COOLDOWN
    public static boolean showCooldown = true;
    public static boolean showCooldownIdle = false;

    // TNT COUNTDOWN
    public static boolean showTntCountdown = true;

    // BLOCK OUTLINE
    public static boolean thickBlockOutline = true;
    public static int blockOutlineExpansion = 25;

    // FREELOOK
    public static boolean freelookActive = false;
    public static float freelookYaw = 0;
    public static float freelookPitch = 0;

    // WEATHER CHANGER
    public static boolean weatherChanger = false;
    public static String weatherType = "off";

    // TIME CHANGER
    public static boolean timeChanger = false;
    public static int timeValue = -1;

    // FOG CUSTOMIZER
    public static boolean fogCustomizer = false;
    public static float fogStart = 10.0f;
    public static float fogEnd = 200.0f;

    // CHAT TABS
    public static boolean chatTabs = false;
    public static String activeChatTab = "all";

    // CHAT HEADS
    public static boolean chatHeads = false;

    // CUSTOM F3
    public static boolean customF3 = false;
    public static boolean f3ShowChunk = true;
    public static boolean f3ShowBiome = true;
    public static boolean f3ShowLight = true;
    public static boolean f3ShowEntities = true;
    public static boolean f3ShowLooking = true;
    public static boolean f3ShowSounds = true;
    public static boolean f3ShowPing = true;
    public static boolean f3ShowMem = true;
    public static boolean f3ShowSystemInfo = true;

    public static class WaypointData {
        public String name;
        public int x, y, z;
        public String dimension;

        public WaypointData() {}
        public WaypointData(String name, int x, int y, int z, String dimension) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
    }

    public static Map<String, Integer> keybinds = new HashMap<>();

    public static int getKeybind(String id) { return keybinds.getOrDefault(id, -1); }
    public static void setKeybind(String id, int key) {
        if (key <= 0) keybinds.remove(id);
        else keybinds.put(id, key);
    }

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
        hugoAutoInvsee = d.hugoAutoInvsee;
        antiBlockRotation = d.antiBlockRotation;
        donutAdminHud = d.donutAdminHud;
        donutCoordSnapper = d.donutCoordSnapper;
        donutRegionMap = d.donutRegionMap;
        donutBalanceTracker = d.donutBalanceTracker;
        donutEchestViewer = d.donutEchestViewer;
        donutTotemWarning = d.donutTotemWarning;
        donutChestFilter = d.donutChestFilter;
        donutAntiScam = d.donutAntiScam;
        donutSpawnerNotifier = d.donutSpawnerNotifier;
        showWaypoints = d.showWaypoints != null ? d.showWaypoints : true;
        waypoints = d.waypoints != null ? d.waypoints : new HashMap<>();
        showStopwatch = d.showStopwatch;
        showClock = d.showClock;
        showDayCounter = d.showDayCounter;
        showPlaytime = d.showPlaytime;
        showMemory = d.showMemory;
        showServerAddress = d.showServerAddress;
        showPingGraph = d.showPingGraph;
        showCooldown = d.showCooldown;
        showCooldownIdle = d.showCooldownIdle;
        showTntCountdown = d.showTntCountdown;
        thickBlockOutline = d.thickBlockOutline;
        blockOutlineExpansion = d.blockOutlineExpansion;
        freelookActive = d.freelookActive;
        freelookYaw = d.freelookYaw;
        freelookPitch = d.freelookPitch;
        weatherChanger = d.weatherChanger;
        weatherType = d.weatherType != null ? d.weatherType : "off";
        timeChanger = d.timeChanger;
        timeValue = d.timeValue;
        fogCustomizer = d.fogCustomizer;
        fogStart = d.fogStart;
        fogEnd = d.fogEnd;
        chatTabs = d.chatTabs;
        activeChatTab = d.activeChatTab != null ? d.activeChatTab : "all";
        chatHeads = d.chatHeads;
        customF3 = d.customF3;
        f3ShowChunk = d.f3ShowChunk;
        f3ShowBiome = d.f3ShowBiome;
        f3ShowLight = d.f3ShowLight;
        f3ShowEntities = d.f3ShowEntities;
        f3ShowLooking = d.f3ShowLooking;
        f3ShowSounds = d.f3ShowSounds;
        f3ShowPing = d.f3ShowPing;
        f3ShowMem = d.f3ShowMem;
        f3ShowSystemInfo = d.f3ShowSystemInfo;
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
        d.hugoAutoInvsee = hugoAutoInvsee;
        d.antiBlockRotation = antiBlockRotation;
        d.donutAdminHud = donutAdminHud;
        d.donutCoordSnapper = donutCoordSnapper;
        d.donutRegionMap = donutRegionMap;
        d.donutBalanceTracker = donutBalanceTracker;
        d.donutEchestViewer = donutEchestViewer;
        d.donutTotemWarning = donutTotemWarning;
        d.donutChestFilter = donutChestFilter;
        d.donutAntiScam = donutAntiScam;
        d.donutSpawnerNotifier = donutSpawnerNotifier;
        d.showWaypoints = showWaypoints;
        d.waypoints = waypoints;
        d.showStopwatch = showStopwatch;
        d.showClock = showClock;
        d.showDayCounter = showDayCounter;
        d.showPlaytime = showPlaytime;
        d.showMemory = showMemory;
        d.showServerAddress = showServerAddress;
        d.showPingGraph = showPingGraph;
        d.showCooldown = showCooldown;
        d.showCooldownIdle = showCooldownIdle;
        d.showTntCountdown = showTntCountdown;
        d.thickBlockOutline = thickBlockOutline;
        d.blockOutlineExpansion = blockOutlineExpansion;
        d.freelookActive = freelookActive;
        d.freelookYaw = freelookYaw;
        d.freelookPitch = freelookPitch;
        d.weatherChanger = weatherChanger;
        d.weatherType = weatherType;
        d.timeChanger = timeChanger;
        d.timeValue = timeValue;
        d.fogCustomizer = fogCustomizer;
        d.fogStart = fogStart;
        d.fogEnd = fogEnd;
        d.chatTabs = chatTabs;
        d.activeChatTab = activeChatTab;
        d.chatHeads = chatHeads;
        d.customF3 = customF3;
        d.f3ShowChunk = f3ShowChunk;
        d.f3ShowBiome = f3ShowBiome;
        d.f3ShowLight = f3ShowLight;
        d.f3ShowEntities = f3ShowEntities;
        d.f3ShowLooking = f3ShowLooking;
        d.f3ShowSounds = f3ShowSounds;
        d.f3ShowPing = f3ShowPing;
        d.f3ShowMem = f3ShowMem;
        d.f3ShowSystemInfo = f3ShowSystemInfo;
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
        boolean hugoAutoInvsee = true;
        boolean antiBlockRotation = true;
        boolean donutAdminHud = true;
        boolean donutCoordSnapper = true;
        boolean donutRegionMap = true;
        boolean donutBalanceTracker = true;
        boolean donutEchestViewer = true;
        boolean donutTotemWarning = true;
        boolean donutChestFilter = true;
        boolean donutAntiScam = true;
        boolean donutSpawnerNotifier = true;
        Boolean showWaypoints = true;
        Map<String, WaypointData> waypoints = new HashMap<>();
        boolean showStopwatch = true;
        boolean showClock = true;
        boolean showDayCounter = true;
        boolean showPlaytime = true;
        boolean showMemory = true;
        boolean showServerAddress = true;
        boolean showPingGraph = true;
        boolean showCooldown = true;
        boolean showCooldownIdle = false;
        boolean showTntCountdown = true;
        boolean thickBlockOutline = true;
        int blockOutlineExpansion = 25;
        boolean freelookActive = false;
        float freelookYaw = 0;
        float freelookPitch = 0;
        boolean weatherChanger = false;
        String weatherType = "off";
        boolean timeChanger = false;
        int timeValue = -1;
        boolean fogCustomizer = false;
        float fogStart = 10.0f;
        float fogEnd = 200.0f;
        boolean chatTabs = false;
        String activeChatTab = "all";
        boolean chatHeads = false;
        boolean customF3 = false;
        boolean f3ShowChunk = true;
        boolean f3ShowBiome = true;
        boolean f3ShowLight = true;
        boolean f3ShowEntities = true;
        boolean f3ShowLooking = true;
        boolean f3ShowSounds = true;
        boolean f3ShowPing = true;
        boolean f3ShowMem = true;
        boolean f3ShowSystemInfo = true;
        int hudX = 4;
        int hudY = 4;
        float hudScale = 1.0f;
        Map<String, ElementPos> elementPositions = new HashMap<>();
        Map<String, Integer> keybinds = new HashMap<>();
    }
}