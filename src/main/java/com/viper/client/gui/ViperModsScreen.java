package com.viper.client.gui;

import com.viper.client.config.Config;
import com.viper.client.util.Titles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ViperModsScreen extends Screen {

    private static final int SIDEBAR_W = 78;
    private static final int TAB_H = 28;
    private static final int CARD_H = 96;
    private static final int CARD_H_TALL = 120;
    private static final int CARD_GAP = 8;

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_MUTED = 0xFF8A8A9C;
    private static final int COLOR_BG    = 0xEE0a0a12;
    private static final int COLOR_PANEL = 0xFF16121f;
    private static final int COLOR_TOGGLE_OFF = 0xFF3a3a4a;
    private static final int COLOR_KEYBG = 0xFF0a0a12;
    private static final int COLOR_KEYBORDER = 0xFF2a1c3d;

    private int panelX, panelY, panelW, panelH;
    private int activeTab = 0;
    private final String[] TABS = {"HUD", "PVP", "HUGOSMP", "DONUTSMP", "THEME", "CONFIG"};

    private final List<List<ModCard>> TAB_MODULES = new ArrayList<>();
    private static final String[] SIDEBAR_LABELS = {"COSMETICS", "SKINS", "EMOTES", "FRIENDS"};

    private static final String[] THEME_IDS = {"viper", "blue", "green", "red", "yellow", "pink", "white", "vanilla"};
    private static final String[] THEME_NAMES = {"VIPER", "BLUE", "GREEN", "RED", "YELLOW", "PINK", "WHITE", "VANILLA"};
    private static final int[] THEME_COLOR = {0xFFA855F7, 0xFF3B82F6, 0xFF22C55E, 0xFFEF4444, 0xFFFACC15, 0xFFEC4899, 0xFFE5E5E5, 0xFF7c7c7c};

    private static final int[] COLOR_OPTIONS = {
            0xFFA855F7, 0xFFFF3B30, 0xFF23A55A, 0xFF3498DB,
            0xFFFFC107, 0xFFFFFFFF, 0xFFFF69B4, 0xFF00FFFF
    };

    private static final String[] CROSSHAIR_IDS = {
            "classic", "dot", "cross", "big", "circle", "circle-dot",
            "t-style", "bracket", "plus", "x", "corner", "minimal"
    };
    private static final String[] CROSSHAIR_NAMES = {
            "CLASSIC", "DOT", "CROSS", "BIG", "CIRCLE", "CIRCLE-DOT",
            "T-STYLE", "BRACKET", "PLUS", "X", "CORNER", "MINIMAL"
    };

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private String awaitingKeybindId = null;
    private String expandedCard = null;

    private String shareStatusMsg = "";
    private long shareStatusTime = 0;

    private String searchQuery = "";
    private boolean searchFocused = false;
    private int searchX, searchY, searchW, searchH;

    private int editingMacroIndex = -1;
    private String macroEditBuffer = "";

    public ViperModsScreen() { super(Text.literal("Viper V1 — Mods")); }

    @Override
    protected void init() {
        panelW = Math.min(740, this.width - 80);
        panelH = Math.min(480, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        searchW = 160;
        searchH = 20;
        searchX = panelX + panelW - searchW - 40;
        searchY = panelY + 12;

        TAB_MODULES.clear();

        List<ModCard> hud = new ArrayList<>();
        hud.add(new ModCard("watermark", "WATERMARK", "Show Viper V1 logo", () -> Config.showWatermark, v -> Config.showWatermark = v));
        hud.add(new ModCard("fps", "FPS", "Show FPS counter", () -> Config.showFps, v -> Config.showFps = v));
        hud.add(new ModCard("cps", "CPS", "Show clicks per second", () -> Config.showCps, v -> Config.showCps = v));
        hud.add(new ModCard("coords", "COORDS", "Show XYZ position", () -> Config.showCoords, v -> Config.showCoords = v));
        hud.add(new ModCard("ping", "PING", "Show network ping", () -> Config.showPing, v -> Config.showPing = v));
        hud.add(new ModCard("armor", "ARMOR", "Show armor HUD", () -> Config.showArmor, v -> Config.showArmor = v));
        hud.add(new ModCard("potion", "POTIONS", "Show active effects", () -> Config.showPotion, v -> Config.showPotion = v));
        hud.add(new ModCard("keystrokes", "KEYSTROKES", "Show WASD keys + CPS", () -> Config.showKeystrokes, v -> Config.showKeystrokes = v));
        hud.add(new ModCard("reach", "REACH", "Show last hit distance", () -> Config.showReach, v -> Config.showReach = v));
        hud.add(new ModCard("combo", "COMBO", "Show hit combo", () -> Config.showCombo, v -> Config.showCombo = v));
        hud.add(new ModCard("direction", "DIRECTION", "Show compass direction", () -> Config.showDirection, v -> Config.showDirection = v));
        hud.add(new ModCard("speed", "SPEED", "Show movement speed", () -> Config.showSpeed, v -> Config.showSpeed = v));
        hud.add(new ModCard("hudtoggle", "HUD MASTER", "Toggle entire HUD", () -> Config.hudEnabled, v -> Config.hudEnabled = v));
        hud.add(new ModCard("lowhealth", "LOW HEALTH", "Red border on low HP", () -> Config.lowHealthWarning, v -> Config.lowHealthWarning = v));
        hud.add(new ModCard("inventoryhud", "INVENTORY HUD", "Show inventory in HUD", () -> Config.showInventoryHud, v -> Config.showInventoryHud = v));

        ModCard notepadCard = new ModCard("notepad", "NOTEPAD", "Click or keybind to open", () -> true, v -> {});
        notepadCard.isAction = true;
        hud.add(notepadCard);

        hud.add(new ModCard("blockbreakprogress", "BLOCK BREAK", "Show mining progress bar", () -> Config.showBlockBreakProgress, v -> Config.showBlockBreakProgress = v));
        hud.add(new ModCard("saturation", "SATURATION", "Show saturation level", () -> Config.showSaturationBar, v -> Config.showSaturationBar = v));
        hud.add(new ModCard("absorption", "ABSORPTION", "Show absorption hearts", () -> Config.showAbsorptionHearts, v -> Config.showAbsorptionHearts = v));
        hud.add(new ModCard("shieldstatus", "SHIELD STATUS", "Show if enemy is blocking", () -> Config.showShieldStatus, v -> Config.showShieldStatus = v));
        hud.add(new ModCard("waypoints", "WAYPOINTS", "Show closest waypoint (/wp set <name>)", () -> Config.showWaypoints, v -> Config.showWaypoints = v));

        ModCard stopwatchCard = new ModCard("stopwatch", "STOPWATCH", "Toggle + reset with keybinds", () -> Config.showStopwatch, v -> Config.showStopwatch = v);
        stopwatchCard.tall = true;
        hud.add(stopwatchCard);

        hud.add(new ModCard("clock", "CLOCK", "Show real-world time", () -> Config.showClock, v -> Config.showClock = v));
        hud.add(new ModCard("daycounter", "DAY COUNTER", "Days since world start", () -> Config.showDayCounter, v -> Config.showDayCounter = v));
        hud.add(new ModCard("playtime", "PLAYTIME", "Time since game start", () -> Config.showPlaytime, v -> Config.showPlaytime = v));
        hud.add(new ModCard("memory", "MEMORY", "Show RAM usage", () -> Config.showMemory, v -> Config.showMemory = v));
        hud.add(new ModCard("serveraddress", "SERVER IP", "Show server address", () -> Config.showServerAddress, v -> Config.showServerAddress = v));
        hud.add(new ModCard("pinggraph", "PING GRAPH", "Show ping history graph", () -> Config.showPingGraph, v -> Config.showPingGraph = v));
        hud.add(new ModCard("cooldown", "COOLDOWN", "Show item cooldowns", () -> Config.showCooldown, v -> Config.showCooldown = v));
        hud.add(new ModCard("tntcountdown", "TNT COUNTDOWN", "Show TNT fuse timer", () -> Config.showTntCountdown, v -> Config.showTntCountdown = v));
        hud.add(new ModCard("attackindicator", "ATTACK INDICATOR", "Show crit/sweep/cooldown", () -> Config.showAttackIndicator, v -> Config.showAttackIndicator = v));
        hud.add(new ModCard("deathinfo", "DEATH INFO", "Show last death coords", () -> Config.showDeathInfo, v -> Config.showDeathInfo = v));
        hud.add(new ModCard("chatheads", "CHAT HEADS", "Show player prefix in chat", () -> Config.chatHeads, v -> Config.chatHeads = v));
        hud.add(new ModCard("chattabs", "CHAT TABS", "Filter chat by tab", () -> Config.chatTabs, v -> Config.chatTabs = v));

        ModCard titleCard = new ModCard("title", "TITLE", "Click to select your title", () -> Config.showTitle, v -> Config.showTitle = v);
        titleCard.expandable = true;
        hud.add(titleCard);

        TAB_MODULES.add(hud);

        List<ModCard> pvp = new ArrayList<>();
        pvp.add(new ModCard("togglesprint", "TOGGLE SPRINT", "Auto-sprint", () -> Config.toggleSprint, v -> Config.toggleSprint = v));
        pvp.add(new ModCard("togglesneak", "TOGGLE SNEAK", "Auto-sneak", () -> Config.toggleSneak, v -> Config.toggleSneak = v));
        pvp.add(new ModCard("zoom", "ZOOM", "Hold key to zoom", () -> Config.zoomEnabled, v -> Config.zoomEnabled = v));

        ModCard crosshairCard = new ModCard("customcrosshair", "CUSTOM CROSSHAIR", "Click to customize", () -> Config.showCustomCrosshair, v -> Config.showCustomCrosshair = v);
        crosshairCard.expandable = true;
        pvp.add(crosshairCard);

        ModCard targetCard = new ModCard("targetindicator", "TARGET INDICATOR", "Click to customize color", () -> Config.showTargetIndicator, v -> Config.showTargetIndicator = v);
        targetCard.expandable = true;
        pvp.add(targetCard);

        ModCard healthCard = new ModCard("healthindicator", "HEALTH INDICATOR", "Click to customize color", () -> Config.showHealthIndicator, v -> Config.showHealthIndicator = v);
        healthCard.expandable = true;
        pvp.add(healthCard);

        ModCard hitmarkerCard = new ModCard("hitmarker", "HIT MARKER", "Click to customize color", () -> Config.showHitMarker, v -> Config.showHitMarker = v);
        hitmarkerCard.expandable = true;
        pvp.add(hitmarkerCard);

        pvp.add(new ModCard("appleskin", "APPLESKIN", "Food hover info", () -> Config.showAppleskin, v -> Config.showAppleskin = v));
        pvp.add(new ModCard("fullbright", "FULLBRIGHT", "Night vision", () -> Config.fullbright, v -> Config.fullbright = v));
        pvp.add(new ModCard("nofog", "NO FOG", "Remove fog", () -> Config.noFog, v -> Config.noFog = v));
        pvp.add(new ModCard("fpsboost", "FPS BOOST", "Aggressive settings for max FPS", () -> Config.fpsBoost, v -> applyFpsBoost(v)));
        pvp.add(new ModCard("chattimestamps", "CHAT TIMESTAMPS", "Show time in chat", () -> Config.chatTimestamps, v -> Config.chatTimestamps = v));
        pvp.add(new ModCard("crystaloptimizer", "CRYSTAL OPTIMIZER", "Fast crystal place (no render delay)", () -> Config.crystalOptimizer, v -> Config.crystalOptimizer = v));
        pvp.add(new ModCard("anchoroptimizer", "ANCHOR OPTIMIZER", "Fast anchor place (no render delay)", () -> Config.anchorOptimizer, v -> Config.anchorOptimizer = v));
        pvp.add(new ModCard("freecam", "FREECAM", "Camera detaches", () -> Config.freecamEnabled, v -> {
            Config.freecamEnabled = v;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (v && mc.player != null) {
                Config.freecamX = mc.player.getX();
                Config.freecamY = mc.player.getEyeY();
                Config.freecamZ = mc.player.getZ();
                Config.freecamYaw = mc.player.getYaw();
                Config.freecamPitch = mc.player.getPitch();
            }
        }));

        pvp.add(new ModCard("damagetint", "DAMAGE TINT", "Red screen edge on damage", () -> Config.damageTint, v -> Config.damageTint = v));
        pvp.add(new ModCard("totemwarning", "TOTEM WARNING", "Warn when no totem equipped", () -> Config.donutTotemWarning, v -> Config.donutTotemWarning = v));
        pvp.add(new ModCard("weatherchanger", "WEATHER CHANGER", "Client-side weather override", () -> Config.weatherChanger, v -> Config.weatherChanger = v));
        pvp.add(new ModCard("timechanger", "TIME CHANGER", "Client-side time override", () -> Config.timeChanger, v -> Config.timeChanger = v));
        pvp.add(new ModCard("customf3", "CUSTOM F3", "Customize debug screen", () -> Config.customF3, v -> Config.customF3 = v));

        ModCard macrosCard = new ModCard("chatmacros", "CHAT MACROS", "Click to edit presets", () -> true, v -> {});
        macrosCard.expandable = true;
        macrosCard.isAction = true;
        pvp.add(macrosCard);

        TAB_MODULES.add(pvp);

        // HUGOSMP tab
        List<ModCard> hugo = new ArrayList<>();
        hugo.add(new ModCard("hugoinvsee", "AUTO INVSEE", "Auto /invsee on 1v1 match", () -> Config.hugoAutoInvsee, v -> Config.hugoAutoInvsee = v));
        hugo.add(new ModCard("antirotation", "ANTI BLOCK ROTATION", "Scramble block rotations (base-hide)", () -> Config.antiBlockRotation, v -> Config.antiBlockRotation = v));
        TAB_MODULES.add(hugo);

        // DONUTSMP tab
        List<ModCard> donut = new ArrayList<>();
        donut.add(new ModCard("adminhud", "ADMIN HUD", "Show when staff is online", () -> Config.donutAdminHud, v -> Config.donutAdminHud = v));
        donut.add(new ModCard("coordsnapper", "COORD SNAPPER", "Copy coordinates to clipboard", () -> Config.donutCoordSnapper, v -> Config.donutCoordSnapper = v));
        donut.add(new ModCard("regionmap", "REGION MAP", "Show DonutSMP region overlay", () -> Config.donutRegionMap, v -> Config.donutRegionMap = v));
        donut.add(new ModCard("balancetracker", "BALANCE TRACKER", "Show player balance on look", () -> Config.donutBalanceTracker, v -> Config.donutBalanceTracker = v));
        donut.add(new ModCard("echestviewer", "ENDER CHEST VIEWER", "Remember ender chest contents", () -> Config.donutEchestViewer, v -> Config.donutEchestViewer = v));
        donut.add(new ModCard("chestfilter", "CHEST FILTER", "Highlight valuable items in chests", () -> Config.donutChestFilter, v -> Config.donutChestFilter = v));
        donut.add(new ModCard("antiscam", "ANTI-SCAM", "Filter scam messages from chat", () -> Config.donutAntiScam, v -> Config.donutAntiScam = v));
        donut.add(new ModCard("spawnernotifier", "SPAWNER NOTIFIER", "Warn when spawner is nearby", () -> Config.donutSpawnerNotifier, v -> Config.donutSpawnerNotifier = v));
        TAB_MODULES.add(donut);

        TAB_MODULES.add(new ArrayList<>()); // THEME
        TAB_MODULES.add(new ArrayList<>()); // CONFIG
    }

    private void applyFpsBoost(boolean enable) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.options == null) return;
        if (enable && !Config.fpsBoost) {
            Config.fpsPrevRenderDist = mc.options.getViewDistance().getValue();
            Config.fpsPrevSimDist = mc.options.getSimulationDistance().getValue();
            Config.fpsPrevEntityDist = (int) (mc.options.getEntityDistanceScaling().getValue() * 100);
            mc.options.getViewDistance().setValue(2);
            mc.options.getSimulationDistance().setValue(4);
            mc.options.getEntityDistanceScaling().setValue(0.5);
            mc.options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.OFF);
            Config.fpsBoost = true;
        } else if (!enable && Config.fpsBoost) {
            mc.options.getViewDistance().setValue(Config.fpsPrevRenderDist);
            mc.options.getSimulationDistance().setValue(Config.fpsPrevSimDist);
            mc.options.getEntityDistanceScaling().setValue(Config.fpsPrevEntityDist / 100.0);
            mc.options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.FANCY);
            Config.fpsBoost = false;
        }
        Config.save();
    }

    private List<ModCard> filterCards(List<ModCard> cards) {
        if (searchQuery.isEmpty()) return cards;
        String q = searchQuery.toLowerCase();
        List<ModCard> out = new ArrayList<>();
        for (ModCard c : cards) {
            if (c.title.toLowerCase().contains(q) || c.description.toLowerCase().contains(q)) {
                out.add(c);
            }
        }
        return out;
    }

    private int cardHeight(ModCard card) {
        if (expandedCard != null && expandedCard.equals(card.id) && card.expandable) {
            return CARD_H + dropdownHeight(card);
        }
        if (card.tall) return CARD_H_TALL;
        return CARD_H;
    }

    private int dropdownHeight(ModCard card) {
        switch (card.id) {
            case "customcrosshair": return 90 + 34 + 40;
            case "targetindicator":
            case "healthindicator":
            case "hitmarker": return 20 + 34;
            case "title": return 24 + (Titles.ALL_TITLES.length * 18) + 10;
            case "chatmacros": return 20 + (6 * 40) + 8;
        }
        return 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();
        int accentDark = Config.getAccentDark();
        int accentBg = Config.getAccentBg();
        int accentHover = Config.getAccentHover();

        context.fill(0, 0, this.width, this.height, 0xBB000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, COLOR_BG);

        context.fill(panelX, panelY, panelX + panelW, panelY + 1, accentDark);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, accentDark);
        context.fill(panelX, panelY, panelX + 1, panelY + panelH, accentDark);
        context.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, accentDark);

        context.fill(panelX, panelY, panelX + SIDEBAR_W, panelY + panelH, COLOR_PANEL);
        context.fill(panelX + SIDEBAR_W, panelY, panelX + SIDEBAR_W + 1, panelY + panelH, accentDark);

        drawSidebar(context, mouseX, mouseY, accent, accentBg, accentHover);

        int tabsX = panelX + SIDEBAR_W + 12;
        int tx = tabsX;
        for (int i = 0; i < TABS.length; i++) {
            String tab = TABS[i];
            int tw = this.textRenderer.getWidth(tab) + 20;
            boolean active = (i == activeTab);
            boolean locked = false;
            if (i == 2 && !com.viper.client.util.ServerTabs.showHugoTab()) locked = true;
            if (i == 3 && !com.viper.client.util.ServerTabs.showDonutTab()) locked = true;
            int textColor = locked ? 0xFF4a4a5a : (active ? COLOR_WHITE : COLOR_MUTED);
            if (active) {
                context.fill(tx, panelY + 8, tx + tw, panelY + 32, accentBg);
                context.drawText(this.textRenderer, "§l" + tab, tx + 10, panelY + 16, textColor, false);
                context.fill(tx, panelY + 32, tx + tw, panelY + 33, accent);
            } else {
                context.drawText(this.textRenderer, tab, tx + 10, panelY + 16, textColor, false);
            }
            tx += tw + 4;
        }

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 20 && mouseY >= closeY && mouseY <= closeY + 20;
        if (closeHover) context.fill(closeX, closeY, closeX + 20, closeY + 20, 0xFF3a1a2a);
        context.drawText(this.textRenderer, "§c✕", closeX + 6, closeY + 6, 0xFFFF5555, false);

        int sBorder = searchFocused ? accent : 0xFF2a1c3d;
        context.fill(searchX, searchY, searchX + searchW, searchY + searchH, 0xFF0a0a12);
        context.fill(searchX, searchY, searchX + searchW, searchY + 1, sBorder);
        context.fill(searchX, searchY + searchH - 1, searchX + searchW, searchY + searchH, sBorder);
        context.fill(searchX, searchY, searchX + 1, searchY + searchH, sBorder);
        context.fill(searchX + searchW - 1, searchY, searchX + searchW, searchY + searchH, sBorder);

        String searchDisplay = (searchQuery.isEmpty() && !searchFocused) ? "§7🔍 search..." : "§f" + searchQuery;
        if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
            searchDisplay += "§f_";
        }
        context.drawText(this.textRenderer, searchDisplay, searchX + 6, searchY + 6, COLOR_WHITE, false);

        context.fill(panelX + SIDEBAR_W + 1, panelY + TAB_H + 18, panelX + panelW, panelY + TAB_H + 19, 0xFF2a1a3a);

        if (activeTab == 4) renderThemeTab(context, mouseX, mouseY);
        else if (activeTab == 5) renderConfigTab(context, mouseX, mouseY, accent);
        else if (activeTab < TAB_MODULES.size()) renderCardsTab(context, mouseX, mouseY, accent);

        if (awaitingKeybindId != null) {
            int ow = 300, oh = 80;
            int ox = this.width / 2 - ow / 2;
            int oy = this.height / 2 - oh / 2;
            context.fill(ox, oy, ox + ow, oy + oh, 0xEE0a0a12);
            context.fill(ox, oy, ox + ow, oy + 2, accent);
            context.fill(ox, oy + oh - 2, ox + ow, oy + oh, accent);
            context.fill(ox, oy, ox + 2, oy + oh, accent);
            context.fill(ox + ow - 2, oy, ox + ow, oy + oh, accent);
            context.drawCenteredTextWithShadow(this.textRenderer, "§lPRESS A KEY", this.width / 2, oy + 22, accent);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7for §f" + awaitingKeybindId, this.width / 2, oy + 42, COLOR_WHITE);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7ESC to cancel / DELETE to remove", this.width / 2, oy + 58, COLOR_MUTED);
        }

        if (!shareStatusMsg.isEmpty() && System.currentTimeMillis() - shareStatusTime < 3000) {
            context.drawCenteredTextWithShadow(this.textRenderer, shareStatusMsg, this.width / 2, panelY + panelH - 36, accent);
        }

        context.drawText(this.textRenderer, "§7Click card to toggle | Click name to expand | ESC to close",
                panelX + SIDEBAR_W + 16, panelY + panelH - 16, COLOR_MUTED, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY, int accent, int accentBg, int accentHover) {
        int iconSize = 40;
        int labelH = 12;
        int itemH = iconSize + labelH + 6;
        int gap = 8;
        int startY = panelY + 20;
        int cx = panelX + SIDEBAR_W / 2;

        {
            int iy = startY;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, accentBg);
            context.fill(cx - iconSize / 2, iy, cx - iconSize / 2 + 2, iy + iconSize, accent);
            drawModsIcon(context, cx, iy + iconSize / 2, accent);
            context.drawCenteredTextWithShadow(this.textRenderer, "MODS", cx, iy + iconSize + 2, accent);
        }

        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            boolean hovered = mouseX >= cx - iconSize / 2 && mouseX <= cx + iconSize / 2 && mouseY >= iy && mouseY <= iy + iconSize;
            int bg = hovered ? accentHover : 0x00000000;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, bg);
            drawPlaceholderIcon(context, i, cx, iy + iconSize / 2, hovered ? accent : COLOR_MUTED);
            context.drawCenteredTextWithShadow(this.textRenderer, SIDEBAR_LABELS[i], cx, iy + iconSize + 2, hovered ? accent : COLOR_MUTED);
        }
    }

    private void drawModsIcon(DrawContext context, int cx, int cy, int accent) {
        context.fill(cx - 6, cy - 6, cx + 6, cy + 6, accent);
        context.fill(cx - 3, cy - 3, cx + 3, cy + 3, 0xFF16121f);
    }

    private void drawPlaceholderIcon(DrawContext context, int index, int cx, int cy, int color) {
        switch (index) {
            case 0:
                context.fill(cx - 5, cy - 4, cx + 5, cy + 5, color);
                context.fill(cx - 7, cy - 4, cx - 5, cy + 1, color);
                context.fill(cx + 5, cy - 4, cx + 7, cy + 1, color);
                context.fill(cx - 2, cy - 4, cx + 2, cy - 2, 0xFF16121f);
                break;
            case 1:
                context.fill(cx - 3, cy - 7, cx + 3, cy - 1, color);
                context.fill(cx - 6, cy + 1, cx + 6, cy + 7, color);
                break;
            case 2:
                for (int i = -5; i <= 5; i++) {
                    for (int j = -5; j <= 5; j++) {
                        int d = i * i + j * j;
                        if (d <= 25 && d >= 16) context.fill(cx + i, cy + j, cx + i + 1, cy + j + 1, color);
                    }
                }
                context.fill(cx - 3, cy - 1, cx - 2, cy, color);
                context.fill(cx + 2, cy - 1, cx + 3, cy, color);
                context.fill(cx - 2, cy + 2, cx + 2, cy + 3, color);
                break;
            case 3:
                context.fill(cx - 7, cy - 5, cx - 3, cy - 1, color);
                context.fill(cx - 8, cy - 1, cx - 2, cy + 4, color);
                context.fill(cx + 3, cy - 5, cx + 7, cy - 1, color);
                context.fill(cx + 2, cy - 1, cx + 8, cy + 4, color);
                break;
        }
    }

    private void renderCardsTab(DrawContext context, int mouseX, int mouseY, int accent) {
        List<ModCard> cards = filterCards(TAB_MODULES.get(activeTab));
        int cardsStartX = panelX + SIDEBAR_W + 16;
        int cardsStartY = panelY + TAB_H + 32;
        int cardsW = panelX + panelW - cardsStartX - 16;
        int cardW = (cardsW - CARD_GAP) / 2;
        int visibleTop = cardsStartY;
        int visibleBottom = panelY + panelH - 24;

        if (cards.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, "§7No modules in this tab yet",
                    panelX + panelW / 2 + SIDEBAR_W / 2, panelY + panelH / 2, COLOR_MUTED);
            return;
        }

        int[] rows = new int[(cards.size() + 1) / 2];
        int col = 0, row = 0, rowMaxH = 0;
        for (ModCard card : cards) {
            int h = cardHeight(card);
            if (h > rowMaxH) rowMaxH = h;
            col++;
            if (col >= 2) {
                rows[row] = rowMaxH;
                rowMaxH = 0;
                col = 0;
                row++;
            }
        }
        if (col > 0) rows[row] = rowMaxH;

        int totalH = 0;
        for (int r : rows) totalH += r + CARD_GAP;
        if (totalH > 0) totalH -= CARD_GAP;

        int visibleH = visibleBottom - visibleTop;
        maxScroll = Math.max(0, totalH - visibleH);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        context.enableScissor(cardsStartX, visibleTop, panelX + panelW - 16, visibleBottom);

        int cy2 = cardsStartY - scrollOffset;
        col = 0;
        row = 0;
        for (ModCard card : cards) {
            int cw = cardW;
            int ch = cardHeight(card);
            int cellX = cardsStartX + col * (cardW + CARD_GAP);
            int cellY = cy2;

            boolean hovered = mouseY >= visibleTop && mouseY <= visibleBottom
                    && mouseX >= cellX && mouseX <= cellX + cw
                    && mouseY >= cellY && mouseY <= cellY + ch;

            drawCardFull(context, card, cellX, cellY, cw, ch, hovered, mouseX, mouseY, accent);

            col++;
            if (col >= 2) {
                col = 0;
                cy2 += rows[row] + CARD_GAP;
                row++;
            }
        }

        context.disableScissor();

        if (maxScroll > 0) {
            int barX = panelX + panelW - 8;
            int barY = visibleTop;
            int barH = visibleH;
            context.fill(barX, barY, barX + 4, barY + barH, 0x44FFFFFF);
            float ratio = (float) visibleH / totalH;
            int thumbH = Math.max(20, (int) (barH * ratio));
            int thumbY = barY + (int) ((barH - thumbH) * ((float) scrollOffset / maxScroll));
            context.fill(barX, thumbY, barX + 4, thumbY + thumbH, accent);
        }
    }

    private void renderThemeTab(DrawContext context, int mouseX, int mouseY) {
        int accent = Config.getAccent();
        int x = panelX + SIDEBAR_W + 30;
        int y = panelY + TAB_H + 60;
        int swatchSize = 60;
        int gap = 12;

        context.drawText(this.textRenderer, "§lSELECT THEME", x, y - 30, COLOR_WHITE, false);
        int perRow = 4;
        for (int i = 0; i < THEME_IDS.length; i++) {
            int col = i % perRow;
            int row = i / perRow;
            int sx = x + col * (swatchSize + gap);
            int sy = y + row * (swatchSize + 22 + gap);
            boolean hovered = mouseX >= sx && mouseX <= sx + swatchSize && mouseY >= sy && mouseY <= sy + swatchSize;
            boolean active = Config.theme.equals(THEME_IDS[i]);
            context.fill(sx, sy, sx + swatchSize, sy + swatchSize, THEME_COLOR[i]);
            if (active) {
                context.fill(sx - 3, sy - 3, sx + swatchSize + 3, sy - 1, 0xFFFFFFFF);
                context.fill(sx - 3, sy + swatchSize + 1, sx + swatchSize + 3, sy + swatchSize + 3, 0xFFFFFFFF);
                context.fill(sx - 3, sy - 1, sx - 1, sy + swatchSize + 1, 0xFFFFFFFF);
                context.fill(sx + swatchSize + 1, sy - 1, sx + swatchSize + 3, sy + swatchSize + 1, 0xFFFFFFFF);
            } else if (hovered) {
                context.fill(sx - 2, sy - 2, sx + swatchSize + 2, sy, 0xAAFFFFFF);
                context.fill(sx - 2, sy + swatchSize, sx + swatchSize + 2, sy + swatchSize + 2, 0xAAFFFFFF);
                context.fill(sx - 2, sy, sx, sy + swatchSize, 0xAAFFFFFF);
                context.fill(sx + swatchSize, sy, sx + swatchSize + 2, sy + swatchSize, 0xAAFFFFFF);
            }
            context.drawCenteredTextWithShadow(this.textRenderer, THEME_NAMES[i], sx + swatchSize / 2, sy + swatchSize + 5, active ? accent : COLOR_MUTED);
        }
    }

    private void renderConfigTab(DrawContext context, int mouseX, int mouseY, int accent) {
        int x = panelX + SIDEBAR_W + 30;
        int y = panelY + TAB_H + 50;

        context.drawText(this.textRenderer, "§lCONFIG SHARE", x, y - 30, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§7Share your settings with friends", x, y - 12, COLOR_MUTED, false);

        int btnW = 240, btnH = 36;

        int shareY = y + 10;
        boolean shareHover = mouseX >= x && mouseX <= x + btnW && mouseY >= shareY && mouseY <= shareY + btnH;
        context.fill(x, shareY, x + btnW, shareY + btnH, shareHover ? Config.getAccentBg() : COLOR_PANEL);
        int borderShare = shareHover ? accent : 0xFF2a1c3d;
        context.fill(x, shareY, x + btnW, shareY + 1, borderShare);
        context.fill(x, shareY + btnH - 1, x + btnW, shareY + btnH, borderShare);
        context.fill(x, shareY, x + 1, shareY + btnH, borderShare);
        context.fill(x + btnW - 1, shareY, x + btnW, shareY + btnH, borderShare);
        context.drawCenteredTextWithShadow(this.textRenderer, "§l📤 EXPORT CONFIG", x + btnW / 2, shareY + 12, accent);
        context.drawText(this.textRenderer, "§7Copies your config string to clipboard", x, shareY + btnH + 6, COLOR_MUTED, false);

        int importY = shareY + btnH + 30;
        boolean importHover = mouseX >= x && mouseX <= x + btnW && mouseY >= importY && mouseY <= importY + btnH;
        context.fill(x, importY, x + btnW, importY + btnH, importHover ? Config.getAccentBg() : COLOR_PANEL);
        int borderImport = importHover ? accent : 0xFF2a1c3d;
        context.fill(x, importY, x + btnW, importY + 1, borderImport);
        context.fill(x, importY + btnH - 1, x + btnW, importY + btnH, borderImport);
        context.fill(x, importY, x + 1, importY + btnH, borderImport);
        context.fill(x + btnW - 1, importY, x + btnW, importY + btnH, borderImport);
        context.drawCenteredTextWithShadow(this.textRenderer, "§l📥 IMPORT FROM CLIPBOARD", x + btnW / 2, importY + 12, accent);
        context.drawText(this.textRenderer, "§7Reads config string from clipboard", x, importY + btnH + 6, COLOR_MUTED, false);

        context.drawText(this.textRenderer, "§7How it works:", x, importY + btnH + 40, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§71. Click EXPORT → config code is copied", x, importY + btnH + 56, COLOR_MUTED, false);
        context.drawText(this.textRenderer, "§72. Send the code to a friend", x, importY + btnH + 70, COLOR_MUTED, false);
        context.drawText(this.textRenderer, "§73. Friend copies it + clicks IMPORT", x, importY + btnH + 84, COLOR_MUTED, false);
    }

    private void drawCardFull(DrawContext context, ModCard card, int x, int y, int w, int h, boolean hovered, int mouseX, int mouseY, int accent) {
        context.fill(x, y, x + w, y + h, hovered ? Config.getAccentBg() : 0xFF1c1228);
        if (card.getter.get() || card.isAction) context.fill(x, y, x + 3, y + h, accent);
        if (hovered) {
            context.fill(x, y, x + w, y + 1, accent);
            context.fill(x, y + h - 1, x + w, y + h, accent);
            context.fill(x + w - 1, y, x + w, y + h, accent);
        }

        context.fill(x + 14, y + 14, x + 26, y + 26, (card.getter.get() || card.isAction) ? accent : COLOR_MUTED);
        context.drawText(this.textRenderer, "§l" + card.title, x + 36, y + 12, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§7" + card.description, x + 36, y + 28, COLOR_MUTED, false);

        if (card.isAction && !card.expandable) {
            int ow = 44, oh = 14;
            int ox = x + w - ow - 14;
            int oy = y + 12;
            context.fill(ox, oy, ox + ow, oy + oh, accent);
            context.drawCenteredTextWithShadow(this.textRenderer, "OPEN", ox + ow / 2, oy + 3, COLOR_WHITE);
        } else if (!card.expandable) {
            int tw = 24, th = 12;
            int tx = x + w - tw - 14;
            int ty = y + 12;
            context.fill(tx, ty, tx + tw, ty + th, card.getter.get() ? accent : COLOR_TOGGLE_OFF);
            int dotX = card.getter.get() ? tx + tw - 10 : tx + 2;
            context.fill(dotX, ty + 2, dotX + 8, ty + th - 2, COLOR_WHITE);
        } else {
            boolean expanded = expandedCard != null && expandedCard.equals(card.id);
            context.drawText(this.textRenderer, expanded ? "§7▼" : "§7▶",
                    x + w - 18, y + 12, COLOR_WHITE, false);
        }

        boolean expanded = expandedCard != null && expandedCard.equals(card.id) && card.expandable;
        if (!expanded && !card.expandable) {
            if (card.tall && card.id.equals("stopwatch")) {
                int kbY1 = y + h - 52;
                int kbY2 = y + h - 26;
                int kbH = 18;
                int kbX = x + 14;
                int kbW = w - 28;

                int key1 = Config.getKeybind("stopwatch");
                String keyName1 = key1 > 0 ? getKeyName(key1) : "NONE";
                boolean isSetting1 = "stopwatch".equals(awaitingKeybindId);
                boolean kbHovered1 = mouseX >= kbX && mouseX <= kbX + kbW && mouseY >= kbY1 && mouseY <= kbY1 + kbH;
                context.fill(kbX, kbY1, kbX + kbW, kbY1 + kbH, isSetting1 ? 0xFF3a1a2a : COLOR_KEYBG);
                int kbBorder1 = isSetting1 ? accent : (kbHovered1 ? accent : COLOR_KEYBORDER);
                context.fill(kbX, kbY1, kbX + kbW, kbY1 + 1, kbBorder1);
                context.fill(kbX, kbY1 + kbH - 1, kbX + kbW, kbY1 + kbH, kbBorder1);
                context.fill(kbX, kbY1, kbX + 1, kbY1 + kbH, kbBorder1);
                context.fill(kbX + kbW - 1, kbY1, kbX + kbW, kbY1 + kbH, kbBorder1);
                if (isSetting1) {
                    context.drawCenteredTextWithShadow(this.textRenderer, "§ePRESS A KEY...", kbX + kbW / 2, kbY1 + 5, 0xFFFFC107);
                } else {
                    context.drawText(this.textRenderer, "§7TOGGLE: §f" + keyName1, kbX + 8, kbY1 + 5, COLOR_WHITE, false);
                }

                int key2 = Config.getKeybind("stopwatch_reset");
                String keyName2 = key2 > 0 ? getKeyName(key2) : "NONE";
                boolean isSetting2 = "stopwatch_reset".equals(awaitingKeybindId);
                boolean kbHovered2 = mouseX >= kbX && mouseX <= kbX + kbW && mouseY >= kbY2 && mouseY <= kbY2 + kbH;
                context.fill(kbX, kbY2, kbX + kbW, kbY2 + kbH, isSetting2 ? 0xFF3a1a2a : COLOR_KEYBG);
                int kbBorder2 = isSetting2 ? accent : (kbHovered2 ? accent : COLOR_KEYBORDER);
                context.fill(kbX, kbY2, kbX + kbW, kbY2 + 1, kbBorder2);
                context.fill(kbX, kbY2 + kbH - 1, kbX + kbW, kbY2 + kbH, kbBorder2);
                context.fill(kbX, kbY2, kbX + 1, kbY2 + kbH, kbBorder2);
                context.fill(kbX + kbW - 1, kbY2, kbX + kbW, kbY2 + kbH, kbBorder2);
                if (isSetting2) {
                    context.drawCenteredTextWithShadow(this.textRenderer, "§ePRESS A KEY...", kbX + kbW / 2, kbY2 + 5, 0xFFFFC107);
                } else {
                    context.drawText(this.textRenderer, "§7RESET: §f" + keyName2, kbX + 8, kbY2 + 5, COLOR_WHITE, false);
                }
            } else {
                int kbY = y + h - 26;
                int kbH = 18;
                int kbX = x + 14;
                int kbW = w - 28;
                int key = Config.getKeybind(card.id);
                String keyName = key > 0 ? getKeyName(key) : "NONE";
                boolean isSetting = card.id.equals(awaitingKeybindId);
                boolean kbHovered = mouseX >= kbX && mouseX <= kbX + kbW && mouseY >= kbY && mouseY <= kbY + kbH;
                context.fill(kbX, kbY, kbX + kbW, kbY + kbH, isSetting ? 0xFF3a1a2a : COLOR_KEYBG);
                int kbBorder = isSetting ? accent : (kbHovered ? accent : COLOR_KEYBORDER);
                context.fill(kbX, kbY, kbX + kbW, kbY + 1, kbBorder);
                context.fill(kbX, kbY + kbH - 1, kbX + kbW, kbY + kbH, kbBorder);
                context.fill(kbX, kbY, kbX + 1, kbY + kbH, kbBorder);
                context.fill(kbX + kbW - 1, kbY, kbX + kbW, kbY + kbH, kbBorder);
                if (isSetting) {
                    context.drawCenteredTextWithShadow(this.textRenderer, "§ePRESS A KEY...", kbX + kbW / 2, kbY + 5, 0xFFFFC107);
                } else {
                    context.drawText(this.textRenderer, "§7key: §f" + keyName, kbX + 8, kbY + 5, COLOR_WHITE, false);
                }
            }
        } else if (expanded) {
            drawDropdown(context, card, x, y + CARD_H, w, accent, mouseX, mouseY);
        }
    }

    private void drawDropdown(DrawContext context, ModCard card, int x, int y, int w, int accent, int mouseX, int mouseY) {
        int dh = dropdownHeight(card);
        context.fill(x, y, x + w, y + dh, 0xFF0a0a12);

        if (card.id.equals("customcrosshair")) {
            context.drawText(this.textRenderer, "§lPRESET", x + 14, y + 8, COLOR_WHITE, false);
            int swatchSize = 34;
            int gap = 6;
            int perRow = 4;
            int startY = y + 22;
            for (int i = 0; i < CROSSHAIR_IDS.length; i++) {
                int col = i % perRow;
                int row = i / perRow;
                int sx = x + 14 + col * (swatchSize + gap);
                int sy = startY + row * (swatchSize + 12 + gap);
                boolean hovered = mouseX >= sx && mouseX <= sx + swatchSize && mouseY >= sy && mouseY <= sy + swatchSize;
                boolean active = Config.crosshairPreset.equals(CROSSHAIR_IDS[i]);
                context.fill(sx, sy, sx + swatchSize, sy + swatchSize, 0xFF16121f);
                if (active) {
                    context.fill(sx - 1, sy - 1, sx + swatchSize + 1, sy, 0xFFFFFFFF);
                    context.fill(sx - 1, sy + swatchSize, sx + swatchSize + 1, sy + swatchSize + 1, 0xFFFFFFFF);
                    context.fill(sx - 1, sy, sx, sy + swatchSize, 0xFFFFFFFF);
                    context.fill(sx + swatchSize, sy, sx + swatchSize + 1, sy + swatchSize, 0xFFFFFFFF);
                } else if (hovered) {
                    context.fill(sx - 1, sy - 1, sx + swatchSize + 1, sy, 0x66FFFFFF);
                    context.fill(sx - 1, sy + swatchSize, sx + swatchSize + 1, sy + swatchSize + 1, 0x66FFFFFF);
                    context.fill(sx - 1, sy, sx, sy + swatchSize, 0x66FFFFFF);
                    context.fill(sx + swatchSize, sy, sx + swatchSize + 1, sy + swatchSize, 0x66FFFFFF);
                }
                drawCrosshairPreview(context, sx + swatchSize / 2, sy + swatchSize / 2, accent, CROSSHAIR_IDS[i]);
                context.drawCenteredTextWithShadow(this.textRenderer, CROSSHAIR_NAMES[i], sx + swatchSize / 2, sy + swatchSize + 2,
                        active ? accent : COLOR_MUTED);
            }
            int rows = (CROSSHAIR_IDS.length + perRow - 1) / perRow;
            int colorY = startY + rows * (swatchSize + 12 + gap) + 4;
            context.drawText(this.textRenderer, "§lCOLOR", x + 14, colorY, COLOR_WHITE, false);
            drawColorRowInline(context, x + 14, colorY + 14, accent, true, mouseX, mouseY);
        } else if (card.id.equals("targetindicator")) {
            context.drawText(this.textRenderer, "§lTARGET COLOR", x + 14, y + 8, COLOR_WHITE, false);
            drawColorRowInline(context, x + 14, y + 22, accent, false, mouseX, mouseY);
        } else if (card.id.equals("healthindicator")) {
            context.drawText(this.textRenderer, "§lHEALTH COLOR", x + 14, y + 8, COLOR_WHITE, false);
            drawColorRowInline(context, x + 14, y + 22, accent, false, mouseX, mouseY);
        } else if (card.id.equals("hitmarker")) {
            context.drawText(this.textRenderer, "§lHIT MARKER COLOR", x + 14, y + 8, COLOR_WHITE, false);
            drawColorRowInline(context, x + 14, y + 22, accent, false, mouseX, mouseY);
        } else if (card.id.equals("title")) {
            drawTitleDropdown(context, x, y, w, accent, mouseX, mouseY);
        } else if (card.id.equals("chatmacros")) {
            drawChatMacrosDropdown(context, x, y, w, accent, mouseX, mouseY);
        }
    }

    private void drawChatMacrosDropdown(DrawContext context, int x, int y, int w, int accent, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, "§lCHAT MACROS", x + 14, y + 6, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§7click text to edit · click key to bind", x + 120, y + 6, COLOR_MUTED, false);

        int rowH = 40;
        int startY = y + 24;
        for (int i = 0; i < 6; i++) {
            int ry = startY + i * rowH;
            boolean enabled = Config.macroEnabled[i];
            boolean editing = (editingMacroIndex == i);
            String text = Config.macroTexts[i];
            if (text == null) text = "";

            context.fill(x + 8, ry, x + w - 8, ry + rowH - 4, enabled ? 0xFF1c1228 : 0xFF14141c);
            if (enabled) context.fill(x + 8, ry, x + 11, ry + rowH - 4, accent);

            int tX = x + 16;
            int tY = ry + 12;
            int tW = 12, tH = 12;
            context.fill(tX, tY, tX + tW, tY + tH, enabled ? accent : COLOR_TOGGLE_OFF);

            int inputX = x + 36;
            int inputY = ry + 8;
            int inputW = w - 36 - 80 - 24;
            int inputH = 20;
            int inputBorder = editing ? accent : COLOR_KEYBORDER;
            context.fill(inputX, inputY, inputX + inputW, inputY + inputH, COLOR_KEYBG);
            context.fill(inputX, inputY, inputX + inputW, inputY + 1, inputBorder);
            context.fill(inputX, inputY + inputH - 1, inputX + inputW, inputY + inputH, inputBorder);
            context.fill(inputX, inputY, inputX + 1, inputY + inputH, inputBorder);
            context.fill(inputX + inputW - 1, inputY, inputX + inputW, inputY + inputH, inputBorder);

            String display;
            if (editing) display = "§f" + macroEditBuffer + ((System.currentTimeMillis() / 500) % 2 == 0 ? "§f_" : "");
            else if (text.isEmpty()) display = "§7click to type...";
            else display = "§f" + text;
            context.drawText(this.textRenderer, display, inputX + 5, inputY + 6, COLOR_WHITE, false);

            int kbX = x + w - 80 - 16;
            int kbY = ry + 8;
            int kbW = 80;
            int kbH = 20;
            int key = Config.getKeybind("macro" + (i + 1));
            String keyName = key > 0 ? getKeyName(key) : "NONE";
            boolean isSetting = ("macro" + (i + 1)).equals(awaitingKeybindId);
            int kbBorder = isSetting ? accent : COLOR_KEYBORDER;
            context.fill(kbX, kbY, kbX + kbW, kbY + kbH, isSetting ? 0xFF3a1a2a : COLOR_KEYBG);
            context.fill(kbX, kbY, kbX + kbW, kbY + 1, kbBorder);
            context.fill(kbX, kbY + kbH - 1, kbX + kbW, kbY + kbH, kbBorder);
            context.fill(kbX, kbY, kbX + 1, kbY + kbH, kbBorder);
            context.fill(kbX + kbW - 1, kbY, kbX + kbW, kbY + kbH, kbBorder);
            if (isSetting) {
                context.drawCenteredTextWithShadow(this.textRenderer, "§e...", kbX + kbW / 2, kbY + 6, 0xFFFFC107);
            } else {
                context.drawText(this.textRenderer, "§7" + keyName, kbX + 6, kbY + 6, COLOR_WHITE, false);
            }
        }
    }

    private void drawTitleDropdown(DrawContext context, int x, int y, int w, int accent, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, "§lSELECT TITLE", x + 14, y + 8, COLOR_WHITE, false);

        MinecraftClient mc = MinecraftClient.getInstance();
        String username = mc.player != null ? mc.player.getName().getString() : null;

        int rowH = 18;
        int startY = y + 24;
        for (int i = 0; i < Titles.ALL_TITLES.length; i++) {
            Titles.Title t = Titles.ALL_TITLES[i];
            int rowY = startY + i * rowH;
            boolean hovered = mouseX >= x + 8 && mouseX <= x + w - 8 && mouseY >= rowY && mouseY <= rowY + rowH - 2;
            boolean selected = t.id.equals(Config.currentTitle);
            boolean unlocked = t.isUnlockedFor(username);

            int rowBg = selected ? accent : (hovered && unlocked ? Config.getAccentBg() : 0x00000000);
            if (rowBg != 0) context.fill(x + 8, rowY, x + w - 8, rowY + rowH - 2, rowBg);

            String display = t.name.isEmpty() ? "(none)" : t.name;
            int textColor = unlocked ? t.color : 0xFF5a5a6c;
            String lock = unlocked ? "" : " §8[LOCKED]";
            context.drawText(this.textRenderer, "§l" + display + lock, x + 16, rowY + 5, textColor, false);
        }
    }

    private void drawColorRowInline(DrawContext context, int x, int y, int accent, boolean isCrosshair, int mouseX, int mouseY) {
        int swatchSize = 20;
        int gap = 5;
        int currentColor;
        boolean useCustom;
        if (isCrosshair) {
            currentColor = Config.crosshairColor;
            useCustom = Config.useCustomCrosshairColor;
        } else {
            if ("targetindicator".equals(expandedCard)) { currentColor = Config.targetColor; useCustom = Config.useCustomTargetColor; }
            else if ("healthindicator".equals(expandedCard)) { currentColor = Config.healthIndicatorColor; useCustom = Config.useCustomHealthColor; }
            else if ("hitmarker".equals(expandedCard)) { currentColor = Config.hitMarkerColor; useCustom = Config.useCustomHitMarkerColor; }
            else { currentColor = 0; useCustom = false; }
        }

        for (int i = 0; i < COLOR_OPTIONS.length; i++) {
            int sx = x + i * (swatchSize + gap);
            int sy = y;
            int col = COLOR_OPTIONS[i];
            boolean hovered = mouseX >= sx && mouseX <= sx + swatchSize && mouseY >= sy && mouseY <= sy + swatchSize;
            boolean selected = useCustom && col == currentColor;
            context.fill(sx, sy, sx + swatchSize, sy + swatchSize, col);
            if (selected) {
                context.fill(sx - 1, sy - 1, sx + swatchSize + 1, sy, 0xFFFFFFFF);
                context.fill(sx - 1, sy + swatchSize, sx + swatchSize + 1, sy + swatchSize + 1, 0xFFFFFFFF);
                context.fill(sx - 1, sy, sx, sy + swatchSize, 0xFFFFFFFF);
                context.fill(sx + swatchSize, sy, sx + swatchSize + 1, sy + swatchSize, 0xFFFFFFFF);
            } else if (hovered) {
                context.fill(sx - 1, sy - 1, sx + swatchSize + 1, sy, 0x66FFFFFF);
                context.fill(sx - 1, sy + swatchSize, sx + swatchSize + 1, sy + swatchSize + 1, 0x66FFFFFF);
                context.fill(sx - 1, sy, sx, sy + swatchSize, 0x66FFFFFF);
                context.fill(sx + swatchSize, sy, sx + swatchSize + 1, sy + swatchSize, 0x66FFFFFF);
            }
        }
    }

    private void drawCrosshairPreview(DrawContext context, int cx, int cy, int color, String preset) {
        switch (preset) {
            case "classic": drawSmallCross(context, cx, cy, color, 3, 2); break;
            case "dot": context.fill(cx - 1, cy - 1, cx + 2, cy + 2, color); break;
            case "cross": drawSmallCross(context, cx, cy, color, 3, 2); context.fill(cx, cy, cx + 1, cy + 1, color); break;
            case "big": drawSmallCross(context, cx, cy, color, 6, 2); break;
            case "circle": drawSmallCircle(context, cx, cy, 5, color); break;
            case "circle-dot": drawSmallCircle(context, cx, cy, 5, color); context.fill(cx, cy, cx + 1, cy + 1, color); break;
            case "t-style":
                context.fill(cx, cy - 5, cx + 1, cy - 2, color);
                context.fill(cx - 5, cy, cx - 2, cy + 1, color);
                context.fill(cx + 2, cy, cx + 5, cy + 1, color);
                break;
            case "bracket":
                context.fill(cx - 5, cy - 5, cx - 4, cy + 5, color);
                context.fill(cx - 5, cy - 5, cx - 2, cy - 4, color);
                context.fill(cx - 5, cy + 4, cx - 2, cy + 5, color);
                context.fill(cx + 4, cy - 5, cx + 5, cy + 5, color);
                context.fill(cx + 2, cy - 5, cx + 5, cy - 4, color);
                context.fill(cx + 2, cy + 4, cx + 5, cy + 5, color);
                break;
            case "plus":
                context.fill(cx - 4, cy, cx + 5, cy + 1, color);
                context.fill(cx, cy - 4, cx + 1, cy + 5, color);
                break;
            case "x":
                for (int i = -4; i <= 4; i++) {
                    context.fill(cx + i, cy + i, cx + i + 1, cy + i + 1, color);
                    context.fill(cx + i, cy - i, cx + i + 1, cy - i + 1, color);
                }
                break;
            case "corner":
                context.fill(cx - 5, cy - 5, cx - 2, cy - 4, color);
                context.fill(cx - 5, cy - 5, cx - 4, cy - 2, color);
                context.fill(cx + 2, cy - 5, cx + 5, cy - 4, color);
                context.fill(cx + 4, cy - 5, cx + 5, cy - 2, color);
                context.fill(cx - 5, cy + 4, cx - 2, cy + 5, color);
                context.fill(cx - 5, cy + 2, cx - 4, cy + 5, color);
                context.fill(cx + 2, cy + 4, cx + 5, cy + 5, color);
                context.fill(cx + 4, cy + 2, cx + 5, cy + 5, color);
                break;
            case "minimal":
                context.fill(cx, cy - 1, cx + 1, cy + 2, color);
                break;
        }
    }

    private void drawSmallCross(DrawContext context, int cx, int cy, int color, int len, int gap) {
        context.fill(cx, cy - gap - len, cx + 1, cy - gap, color);
        context.fill(cx, cy + gap, cx + 1, cy + gap + len, color);
        context.fill(cx - gap - len, cy, cx - gap, cy + 1, color);
        context.fill(cx + gap, cy, cx + gap + len, cy + 1, color);
    }

    private void drawSmallCircle(DrawContext context, int cx, int cy, int radius, int color) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int d = i * i + j * j;
                if (d <= radius * radius && d >= (radius - 1) * (radius - 1)) {
                    context.fill(cx + i, cy + j, cx + i + 1, cy + j + 1, color);
                }
            }
        }
    }

    private String getKeyName(int key) {
        if (key <= 0) return "NONE";
        switch (key) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_ESCAPE: return "ESC";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
        }
        if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F12) return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) return String.valueOf((char) ('A' + (key - GLFW.GLFW_KEY_A)));
        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) return String.valueOf((char) ('0' + (key - GLFW.GLFW_KEY_0)));
        return "KEY" + key;
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (editingMacroIndex >= 0) {
            int cp = input.codepoint();
            if (cp >= 32 && macroEditBuffer.length() < 80) {
                macroEditBuffer += (char) cp;
            }
            return true;
        }
        if (searchFocused && awaitingKeybindId == null) {
            int cp = input.codepoint();
            if (cp >= 32) {
                searchQuery += (char) cp;
            }
            return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (editingMacroIndex >= 0) {
            int keyCode = input.key();
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                Config.macroTexts[editingMacroIndex] = macroEditBuffer;
                Config.save();
                editingMacroIndex = -1;
                macroEditBuffer = "";
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                editingMacroIndex = -1;
                macroEditBuffer = "";
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (macroEditBuffer.length() > 0) {
                    macroEditBuffer = macroEditBuffer.substring(0, macroEditBuffer.length() - 1);
                }
                return true;
            }
            return true;
        }
        if (awaitingKeybindId != null) {
            int keyCode = input.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { awaitingKeybindId = null; return true; }
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                Config.setKeybind(awaitingKeybindId, -1); awaitingKeybindId = null; Config.save(); return true;
            }
            Config.setKeybind(awaitingKeybindId, keyCode); awaitingKeybindId = null; Config.save(); return true;
        }
        if (searchFocused) {
            int keyCode = input.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { searchFocused = false; return true; }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && searchQuery.length() > 0) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (awaitingKeybindId != null) return false;
        if (activeTab >= 4) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        if (maxScroll > 0) {
            scrollOffset -= (int) (verticalAmount * 20);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        if (awaitingKeybindId != null) return true;

        int mx = (int) click.x();
        int my = (int) click.y();
        int accent = Config.getAccent();

        if (editingMacroIndex >= 0) {
            Config.macroTexts[editingMacroIndex] = macroEditBuffer;
            Config.save();
            editingMacroIndex = -1;
            macroEditBuffer = "";
        }

        if (mx >= searchX && mx <= searchX + searchW && my >= searchY && my <= searchY + searchH) {
            searchFocused = true;
            return true;
        }
        searchFocused = false;

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        if (mx >= closeX && mx <= closeX + 20 && my >= closeY && my <= closeY + 20) {
            this.client.setScreen(null);
            return true;
        }

        int iconSize = 40;
        int labelH = 12;
        int itemH = iconSize + labelH + 6;
        int gap = 8;
        int startY = panelY + 20;
        int cxSide = panelX + SIDEBAR_W / 2;
        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            if (mx >= cxSide - iconSize / 2 && mx <= cxSide + iconSize / 2 && my >= iy && my <= iy + itemH) {
                if (this.client != null) this.client.setScreen(new ViperPlaceholderScreen(i));
                return true;
            }
        }

        int tabsX = panelX + SIDEBAR_W + 12;
        int tx = tabsX;
        for (int i = 0; i < TABS.length; i++) {
            int tw = this.textRenderer.getWidth(TABS[i]) + 20;
            if (mx >= tx && mx <= tx + tw && my >= panelY + 8 && my <= panelY + 32) {
                boolean blocked = false;
                if (i == 2 && !com.viper.client.util.ServerTabs.showHugoTab()) blocked = true;
                if (i == 3 && !com.viper.client.util.ServerTabs.showDonutTab()) blocked = true;
                if (blocked) return true;
                activeTab = i; scrollOffset = 0; expandedCard = null; return true;
            }
            tx += tw + 4;
        }

        if (activeTab == 4) {
            int x0 = panelX + SIDEBAR_W + 30;
            int y0 = panelY + TAB_H + 60;
            int swatchSize = 60;
            int gap2 = 12;
            int perRow = 4;
            for (int i = 0; i < THEME_IDS.length; i++) {
                int col = i % perRow;
                int row = i / perRow;
                int sx = x0 + col * (swatchSize + gap2);
                int sy = y0 + row * (swatchSize + 22 + gap2);
                if (mx >= sx && mx <= sx + swatchSize && my >= sy && my <= sy + swatchSize) {
                    Config.theme = THEME_IDS[i]; Config.save(); return true;
                }
            }
            return true;
        }

        if (activeTab == 5) {
            int x0 = panelX + SIDEBAR_W + 30;
            int y0 = panelY + TAB_H + 60;
            int btnW = 240;
            int btnH = 36;

            int shareY = y0 + 10;
            if (mx >= x0 && mx <= x0 + btnW && my >= shareY && my <= shareY + btnH) {
                String cfg = Config.exportAsString();
                if (cfg != null && this.client != null) {
                    this.client.keyboard.setClipboard(cfg);
                    shareStatusMsg = "§a✓ Config copied to clipboard! (" + cfg.length() + " chars)";
                    shareStatusTime = System.currentTimeMillis();
                } else {
                    shareStatusMsg = "§c✗ Export failed";
                    shareStatusTime = System.currentTimeMillis();
                }
                return true;
            }

            int importY = shareY + btnH + 30;
            if (mx >= x0 && mx <= x0 + btnW && my >= importY && my <= importY + btnH) {
                if (this.client != null) {
                    String clip = this.client.keyboard.getClipboard();
                    if (clip != null && !clip.isEmpty()) {
                        boolean ok = Config.importFromString(clip);
                        if (ok) {
                            shareStatusMsg = "§a✓ Config imported!";
                            shareStatusTime = System.currentTimeMillis();
                        } else {
                            shareStatusMsg = "§c✗ Invalid config string";
                            shareStatusTime = System.currentTimeMillis();
                        }
                    } else {
                        shareStatusMsg = "§c✗ Clipboard is empty";
                        shareStatusTime = System.currentTimeMillis();
                    }
                }
                return true;
            }
            return true;
        }

        if (activeTab < TAB_MODULES.size()) {
            List<ModCard> cards = filterCards(TAB_MODULES.get(activeTab));
            int cardsStartX = panelX + SIDEBAR_W + 16;
            int cardsStartY = panelY + TAB_H + 32;
            int cardsW = panelX + panelW - cardsStartX - 16;
            int cardW = (cardsW - CARD_GAP) / 2;
            int visibleTop = cardsStartY;
            int visibleBottom = panelY + panelH - 24;

            int[] rows = new int[(cards.size() + 1) / 2];
            int col = 0, row = 0, rowMaxH = 0;
            for (ModCard card : cards) {
                int h = cardHeight(card);
                if (h > rowMaxH) rowMaxH = h;
                col++;
                if (col >= 2) {
                    rows[row] = rowMaxH;
                    rowMaxH = 0;
                    col = 0;
                    row++;
                }
            }
            if (col > 0) rows[row] = rowMaxH;

            int cy2 = cardsStartY - scrollOffset;
            col = 0;
            row = 0;
            for (ModCard card : cards) {
                int cw = cardW;
                int ch = cardHeight(card);
                int cellX = cardsStartX + col * (cardW + CARD_GAP);
                int cellY = cy2;

                if (my >= visibleTop && my <= visibleBottom && mx >= cellX && mx <= cellX + cw
                        && my >= cellY && my <= cellY + ch) {

                    boolean expanded = expandedCard != null && expandedCard.equals(card.id) && card.expandable;

                    if (expanded) {
                        int dropY = cellY + CARD_H;
                        if (handleDropdownClick(card, cellX, dropY, cw, mx, my, accent)) return true;
                    }

                    if (card.expandable) {
                        if (my >= cellY && my <= cellY + 40) {
                            if (expandedCard != null && expandedCard.equals(card.id)) expandedCard = null;
                            else expandedCard = card.id;
                            return true;
                        }
                        return true;
                    }

                    if (card.tall && card.id.equals("stopwatch")) {
                        int kbY1 = cellY + ch - 52;
                        int kbY2 = cellY + ch - 26;
                        int kbH = 18;
                        int kbX = cellX + 14;
                        int kbW = cw - 28;
                        if (my >= kbY1 && my <= kbY1 + kbH && mx >= kbX && mx <= kbX + kbW) {
                            awaitingKeybindId = "stopwatch";
                            return true;
                        }
                        if (my >= kbY2 && my <= kbY2 + kbH && mx >= kbX && mx <= kbX + kbW) {
                            awaitingKeybindId = "stopwatch_reset";
                            return true;
                        }
                    } else {
                        int kbY = cellY + CARD_H - 26;
                        int kbH = 18;
                        int kbX = cellX + 14;
                        int kbW = cw - 28;
                        if (my >= kbY && my <= kbY + kbH && mx >= kbX && mx <= kbX + kbW) {
                            awaitingKeybindId = card.id;
                            return true;
                        }
                    }

                    if (card.isAction) {
                        if ("notepad".equals(card.id)) {
                            if (this.client != null) this.client.setScreen(new NotepadScreen());
                        }
                        return true;
                    }

                    int tw = 24, th = 12;
                    int tx2 = cellX + cw - tw - 14;
                    int ty2 = cellY + 12;
                    if (my >= ty2 - 4 && my <= ty2 + th + 4 && mx >= tx2 - 4 && mx <= tx2 + tw + 4) {
                        boolean newVal = !card.getter.get();
                        card.setter.accept(newVal);
                        Config.save();
                        return true;
                    }

                    boolean newVal = !card.getter.get();
                    card.setter.accept(newVal);
                    Config.save();
                    return true;
                }

                col++;
                if (col >= 2) {
                    col = 0;
                    cy2 += rows[row] + CARD_GAP;
                    row++;
                }
            }
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean handleDropdownClick(ModCard card, int x, int y, int w, int mx, int my, int accent) {
        if (card.id.equals("customcrosshair")) {
            int swatchSize = 34;
            int gap = 6;
            int perRow = 4;
            int startY = y + 22;
            for (int i = 0; i < CROSSHAIR_IDS.length; i++) {
                int col = i % perRow;
                int row = i / perRow;
                int sx = x + 14 + col * (swatchSize + gap);
                int sy = startY + row * (swatchSize + 12 + gap);
                if (mx >= sx && mx <= sx + swatchSize && my >= sy && my <= sy + swatchSize) {
                    Config.crosshairPreset = CROSSHAIR_IDS[i];
                    Config.save();
                    return true;
                }
            }
            int rows = (CROSSHAIR_IDS.length + perRow - 1) / perRow;
            int colorY = startY + rows * (swatchSize + 12 + gap) + 4 + 14;
            return handleColorRowClick(x + 14, colorY, true, mx, my);
        } else if (card.id.equals("targetindicator")) {
            return handleColorRowClick(x + 14, y + 22, false, mx, my);
        } else if (card.id.equals("healthindicator")) {
            return handleColorRowClick(x + 14, y + 22, false, mx, my);
        } else if (card.id.equals("hitmarker")) {
            return handleColorRowClick(x + 14, y + 22, false, mx, my);
        } else if (card.id.equals("title")) {
            return handleTitleClick(x, y, w, mx, my);
        } else if (card.id.equals("chatmacros")) {
            return handleChatMacrosClick(x, y, w, mx, my, accent);
        }
        return false;
    }

    private boolean handleChatMacrosClick(int x, int y, int w, int mx, int my, int accent) {
        int rowH = 40;
        int startY = y + 24;
        for (int i = 0; i < 6; i++) {
            int ry = startY + i * rowH;

            int tX = x + 16;
            int tY = ry + 12;
            int tW = 12, tH = 12;
            if (mx >= tX && mx <= tX + tW && my >= tY && my <= tY + tH) {
                Config.macroEnabled[i] = !Config.macroEnabled[i];
                Config.save();
                return true;
            }

            int inputX = x + 36;
            int inputY = ry + 8;
            int inputW = w - 36 - 80 - 24;
            int inputH = 20;
            if (mx >= inputX && mx <= inputX + inputW && my >= inputY && my <= inputY + inputH) {
                editingMacroIndex = i;
                macroEditBuffer = Config.macroTexts[i] == null ? "" : Config.macroTexts[i];
                return true;
            }

            int kbX = x + w - 80 - 16;
            int kbY = ry + 8;
            int kbW = 80;
            int kbH = 20;
            if (mx >= kbX && mx <= kbX + kbW && my >= kbY && my <= kbY + kbH) {
                awaitingKeybindId = "macro" + (i + 1);
                return true;
            }
        }
        return false;
    }

    private boolean handleTitleClick(int x, int y, int w, int mx, int my) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String username = mc.player != null ? mc.player.getName().getString() : null;

        int rowH = 18;
        int startY = y + 24;
        for (int i = 0; i < Titles.ALL_TITLES.length; i++) {
            Titles.Title t = Titles.ALL_TITLES[i];
            int rowY = startY + i * rowH;
            if (mx >= x + 8 && mx <= x + w - 8 && my >= rowY && my <= rowY + rowH - 2) {
                if (t.isUnlockedFor(username)) {
                    Config.currentTitle = t.id;
                    Config.save();
                }
                return true;
            }
        }
        return false;
    }

    private boolean handleColorRowClick(int x, int y, boolean isCrosshair, int mx, int my) {
        int swatchSize = 20;
        int gap = 5;
        for (int i = 0; i < COLOR_OPTIONS.length; i++) {
            int sx = x + i * (swatchSize + gap);
            int sy = y;
            if (mx >= sx && mx <= sx + swatchSize && my >= sy && my <= sy + swatchSize) {
                int col = COLOR_OPTIONS[i];
                if (isCrosshair) {
                    Config.crosshairColor = col; Config.useCustomCrosshairColor = true;
                } else {
                    if ("targetindicator".equals(expandedCard)) { Config.targetColor = col; Config.useCustomTargetColor = true; }
                    else if ("healthindicator".equals(expandedCard)) { Config.healthIndicatorColor = col; Config.useCustomHealthColor = true; }
                    else if ("hitmarker".equals(expandedCard)) { Config.hitMarkerColor = col; Config.useCustomHitMarkerColor = true; }
                }
                Config.save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() { Config.save(); this.client.setScreen(null); }

    @Override
    public boolean shouldPause() { return false; }

    private static class ModCard {
        String id, title, description;
        boolean expandable = false;
        boolean isAction = false;
        boolean tall = false;
        java.util.function.Supplier<Boolean> getter;
        java.util.function.Consumer<Boolean> setter;
        ModCard(String id, String title, String desc, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
            this.id = id; this.title = title; this.description = desc;
            this.getter = getter; this.setter = setter;
        }
    }
}