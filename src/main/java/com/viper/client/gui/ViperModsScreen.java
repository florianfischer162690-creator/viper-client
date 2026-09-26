package com.viper.client.gui;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ViperModsScreen extends Screen {

    private static final int SIDEBAR_W = 78;
    private static final int TAB_H = 28;
    private static final int CARD_H = 96;
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
    private final String[] TABS = {"HUD", "PVP", "THEME", "COLORS"};

    private final List<List<ModCard>> TAB_MODULES = new ArrayList<>();
    private static final String[] SIDEBAR_LABELS = {"COSMETICS", "SKINS", "EMOTES", "FRIENDS"};

    private static final String[] THEME_IDS = {"viper", "blue", "green", "red", "yellow", "pink", "white", "vanilla"};
    private static final String[] THEME_NAMES = {"VIPER", "BLUE", "GREEN", "RED", "YELLOW", "PINK", "WHITE", "VANILLA"};
    private static final int[] THEME_COLOR = {0xFFA855F7, 0xFF3B82F6, 0xFF22C55E, 0xFFEF4444, 0xFFFACC15, 0xFFEC4899, 0xFFE5E5E5, 0xFF7c7c7c};

    private static final int[] COLOR_OPTIONS = {
            0xFFA855F7, 0xFFFF3B30, 0xFF23A55A, 0xFF3498DB,
            0xFFFFC107, 0xFFFFFFFF, 0xFFFF69B4, 0xFF00FFFF
    };
    private static final String[] COLOR_NAMES = {"LILA", "ROT", "GRÜN", "BLAU", "GELB", "WEISS", "PINK", "CYAN"};

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private String awaitingKeybindId = null;

    public ViperModsScreen() { super(Text.literal("Viper V1 — Mods")); }

    @Override
    protected void init() {
        panelW = Math.min(740, this.width - 80);
        panelH = Math.min(480, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        TAB_MODULES.clear();

        List<ModCard> hud = new ArrayList<>();
        hud.add(new ModCard("watermark", "WATERMARK", "Show Viper V1 logo", () -> Config.showWatermark, v -> Config.showWatermark = v));
        hud.add(new ModCard("fps", "FPS", "Show FPS counter", () -> Config.showFps, v -> Config.showFps = v));
        hud.add(new ModCard("cps", "CPS", "Show clicks per second", () -> Config.showCps, v -> Config.showCps = v));
        hud.add(new ModCard("coords", "COORDS", "Show XYZ position", () -> Config.showCoords, v -> Config.showCoords = v));
        hud.add(new ModCard("ping", "PING", "Show network ping", () -> Config.showPing, v -> Config.showPing = v));
        hud.add(new ModCard("armor", "ARMOR", "Show armor HUD", () -> Config.showArmor, v -> Config.showArmor = v));
        hud.add(new ModCard("potion", "POTIONS", "Show active effects", () -> Config.showPotion, v -> Config.showPotion = v));
        hud.add(new ModCard("keystrokes", "KEYSTROKES", "Show WASD keys", () -> Config.showKeystrokes, v -> Config.showKeystrokes = v));
        hud.add(new ModCard("reach", "REACH", "Show last hit distance", () -> Config.showReach, v -> Config.showReach = v));
        hud.add(new ModCard("combo", "COMBO", "Show hit combo", () -> Config.showCombo, v -> Config.showCombo = v));
        hud.add(new ModCard("hudtoggle", "HUD MASTER", "Toggle entire HUD (F4)", () -> Config.hudEnabled, v -> Config.hudEnabled = v));
        hud.add(new ModCard("lowhealth", "LOW HEALTH", "Red border on low HP", () -> Config.lowHealthWarning, v -> Config.lowHealthWarning = v));
        TAB_MODULES.add(hud);

        List<ModCard> pvp = new ArrayList<>();
        pvp.add(new ModCard("togglesprint", "TOGGLE SPRINT", "Auto-sprint (R)", () -> Config.toggleSprint, v -> Config.toggleSprint = v));
        pvp.add(new ModCard("togglesneak", "TOGGLE SNEAK", "Auto-sneak", () -> Config.toggleSneak, v -> Config.toggleSneak = v));
        pvp.add(new ModCard("customcrosshair", "CUSTOM CROSSHAIR", "Custom crosshair", () -> Config.showCustomCrosshair, v -> Config.showCustomCrosshair = v));
        pvp.add(new ModCard("targetindicator", "TARGET INDICATOR", "Box when aiming entity", () -> Config.showTargetIndicator, v -> Config.showTargetIndicator = v));
        pvp.add(new ModCard("healthindicator", "HEALTH OVER ENTITY", "Show HP over target", () -> Config.showHealthIndicator, v -> Config.showHealthIndicator = v));
        pvp.add(new ModCard("fullbright", "FULLBRIGHT", "Night vision (F8)", () -> Config.fullbright, v -> {
            Config.fullbright = v;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.options != null) mc.options.getGamma().setValue(v ? 10.0 : 1.0);
        }));
        pvp.add(new ModCard("fpsboost", "FPS BOOST", "Aggressive settings for max FPS", () -> Config.fpsBoost, v -> applyFpsBoost(v)));
        TAB_MODULES.add(pvp);

        TAB_MODULES.add(new ArrayList<>());
        TAB_MODULES.add(new ArrayList<>());

        scrollOffset = 0;
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();
        int accentLight = Config.getAccentLight();
        int accentDark = Config.getAccentDark();

        context.fill(0, 0, this.width, this.height, 0xBB000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, COLOR_BG);

        context.fill(panelX, panelY, panelX + panelW, panelY + 1, accentDark);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, accentDark);
        context.fill(panelX, panelY, panelX + 1, panelY + panelH, accentDark);
        context.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, accentDark);

        context.fill(panelX, panelY, panelX + SIDEBAR_W, panelY + panelH, COLOR_PANEL);
        context.fill(panelX + SIDEBAR_W, panelY, panelX + SIDEBAR_W + 1, panelY + panelH, accentDark);

        drawSidebar(context, mouseX, mouseY, accent, accentLight);

        int tabsX = panelX + SIDEBAR_W + 16;
        int tx = tabsX;
        for (int i = 0; i < TABS.length; i++) {
            String tab = TABS[i];
            int tw = this.textRenderer.getWidth(tab) + 24;
            boolean active = (i == activeTab);
            int textColor = active ? COLOR_WHITE : COLOR_MUTED;
            if (active) {
                context.fill(tx, panelY + 8, tx + tw, panelY + 32, 0xFF221533);
                context.drawText(this.textRenderer, "§l" + tab, tx + 12, panelY + 16, textColor, false);
                context.fill(tx, panelY + 32, tx + tw, panelY + 33, accent);
            } else {
                context.drawText(this.textRenderer, tab, tx + 12, panelY + 16, textColor, false);
            }
            tx += tw + 8;
        }

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 20 && mouseY >= closeY && mouseY <= closeY + 20;
        if (closeHover) context.fill(closeX, closeY, closeX + 20, closeY + 20, 0xFF3a1a2a);
        context.drawText(this.textRenderer, "§c✕", closeX + 6, closeY + 6, 0xFFFF5555, false);

        context.fill(panelX + SIDEBAR_W + 1, panelY + TAB_H + 18, panelX + panelW, panelY + TAB_H + 19, 0xFF2a1a3a);

        if (activeTab == 2) {
            renderThemeTab(context, mouseX, mouseY);
        } else if (activeTab == 3) {
            renderColorsTab(context, mouseX, mouseY);
        } else if (activeTab < TAB_MODULES.size()) {
            renderCardsTab(context, mouseX, mouseY, accent);
        }

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

        context.drawText(this.textRenderer, "§7Click card to toggle | Click key to set | ESC to close",
                panelX + SIDEBAR_W + 16, panelY + panelH - 16, COLOR_MUTED, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCardsTab(DrawContext context, int mouseX, int mouseY, int accent) {
        List<ModCard> cards = TAB_MODULES.get(activeTab);
        int cardsStartX = panelX + SIDEBAR_W + 16;
        int cardsStartY = panelY + TAB_H + 32;
        int cardsW = panelX + panelW - cardsStartX - 16;
        int cardW = (cardsW - CARD_GAP) / 2;
        int visibleTop = cardsStartY;
        int visibleBottom = panelY + panelH - 24;

        int rows = (cards.size() + 1) / 2;
        int totalH = rows * (CARD_H + CARD_GAP) - CARD_GAP;
        int visibleH = visibleBottom - visibleTop;
        maxScroll = Math.max(0, totalH - visibleH);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        context.enableScissor(cardsStartX, visibleTop, panelX + panelW - 16, visibleBottom);

        int col = 0, row = 0;
        for (ModCard card : cards) {
            int cx = cardsStartX + col * (cardW + CARD_GAP);
            int cy = cardsStartY + row * (CARD_H + CARD_GAP) - scrollOffset;
            boolean hovered = mouseY >= visibleTop && mouseY <= visibleBottom
                    && mouseX >= cx && mouseX <= cx + cardW
                    && mouseY >= cy && mouseY <= cy + CARD_H;
            drawCard(context, card, cx, cy, cardW, CARD_H, hovered, mouseX, mouseY, accent);
            col++;
            if (col >= 2) { col = 0; row++; }
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

    private void renderColorsTab(DrawContext context, int mouseX, int mouseY) {
        int accent = Config.getAccent();
        int x = panelX + SIDEBAR_W + 30;
        int y = panelY + TAB_H + 45;
        int rowH = 70;

        String[] labels = {"CROSSHAIR COLOR", "TARGET COLOR", "HEALTH BAR COLOR"};
        int[] currentColors = {Config.crosshairColor, Config.targetColor, Config.healthIndicatorColor};
        boolean[] useCustom = {Config.useCustomCrosshairColor, Config.useCustomTargetColor, Config.useCustomHealthColor};

        for (int r = 0; r < 3; r++) {
            int ry = y + r * rowH;

            String status = useCustom[r] ? " §a(custom)" : " §7(theme)";
            context.drawText(this.textRenderer, "§l" + labels[r] + status, x, ry - 14, COLOR_WHITE, false);

            int swatchSize = 22;
            int swatchGap = 6;
            for (int c = 0; c < COLOR_OPTIONS.length; c++) {
                int sx = x + c * (swatchSize + swatchGap);
                int sy = ry;
                int col = COLOR_OPTIONS[c];
                boolean hovered = mouseX >= sx && mouseX <= sx + swatchSize && mouseY >= sy && mouseY <= sy + swatchSize;
                boolean isSelected = useCustom[r] && (col == currentColors[r]);

                context.fill(sx, sy, sx + swatchSize, sy + swatchSize, col);

                if (isSelected) {
                    context.fill(sx - 2, sy - 2, sx + swatchSize + 2, sy - 1, 0xFFFFFFFF);
                    context.fill(sx - 2, sy + swatchSize + 1, sx + swatchSize + 2, sy + swatchSize + 2, 0xFFFFFFFF);
                    context.fill(sx - 2, sy - 1, sx - 1, sy + swatchSize + 1, 0xFFFFFFFF);
                    context.fill(sx + swatchSize + 1, sy - 1, sx + swatchSize + 2, sy + swatchSize + 1, 0xFFFFFFFF);
                } else if (hovered) {
                    context.fill(sx - 1, sy - 1, sx + swatchSize + 1, sy, 0xAAFFFFFF);
                    context.fill(sx - 1, sy + swatchSize, sx + swatchSize + 1, sy + swatchSize + 1, 0xAAFFFFFF);
                    context.fill(sx - 1, sy, sx, sy + swatchSize, 0xAAFFFFFF);
                    context.fill(sx + swatchSize, sy, sx + swatchSize + 1, sy + swatchSize, 0xAAFFFFFF);
                }
            }

            int btnX = x + (swatchSize + swatchGap) * COLOR_OPTIONS.length + 10;
            int btnY = ry;
            int btnW = 80;
            int btnH = swatchSize;
            boolean btnHover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

            context.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnHover ? 0xFF2a1a44 : COLOR_PANEL);
            int border = btnHover ? accent : 0xFF2a1c3d;
            context.fill(btnX, btnY, btnX + btnW, btnY + 1, border);
            context.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, border);
            context.fill(btnX, btnY, btnX + 1, btnY + btnH, border);
            context.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, border);

            String btnLabel = useCustom[r] ? "RESET" : "THEME";
            context.drawCenteredTextWithShadow(this.textRenderer, btnLabel, btnX + btnW / 2, btnY + 7, useCustom[r] ? COLOR_MUTED : accent);
        }
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY, int accent, int accentLight) {
        int iconSize = 40;
        int labelH = 12;
        int itemH = iconSize + labelH + 6;
        int gap = 8;
        int startY = panelY + 20;
        int cx = panelX + SIDEBAR_W / 2;

        {
            int iy = startY;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, 0xFF2a1a44);
            context.fill(cx - iconSize / 2, iy, cx - iconSize / 2 + 2, iy + iconSize, accent);
            drawModsIcon(context, cx, iy + iconSize / 2, accent);
            context.drawCenteredTextWithShadow(this.textRenderer, "MODS", cx, iy + iconSize + 2, accent);
        }

        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            boolean hovered = mouseX >= cx - iconSize / 2 && mouseX <= cx + iconSize / 2 && mouseY >= iy && mouseY <= iy + iconSize;
            int bg = hovered ? 0x33A855F7 : 0x00000000;
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

    private void drawCard(DrawContext context, ModCard card, int x, int y, int w, int h, boolean hovered, int mouseX, int mouseY, int accent) {
        context.fill(x, y, x + w, y + h, hovered ? 0xFF2a1a44 : 0xFF1c1228);

        if (card.getter.get()) {
            context.fill(x, y, x + 3, y + h, accent);
        }
        if (hovered) {
            context.fill(x, y, x + w, y + 1, accent);
            context.fill(x, y + h - 1, x + w, y + h, accent);
            context.fill(x + w - 1, y, x + w, y + h, accent);
        }

        context.fill(x + 14, y + 14, x + 26, y + 26, card.getter.get() ? accent : COLOR_MUTED);

        context.drawText(this.textRenderer, "§l" + card.title, x + 36, y + 12, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§7" + card.description, x + 36, y + 28, COLOR_MUTED, false);

        int tw = 24, th = 12;
        int tx = x + w - tw - 14;
        int ty = y + 12;
        context.fill(tx, ty, tx + tw, ty + th, card.getter.get() ? accent : COLOR_TOGGLE_OFF);
        int dotX = card.getter.get() ? tx + tw - 10 : tx + 2;
        context.fill(dotX, ty + 2, dotX + 8, ty + th - 2, COLOR_WHITE);

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
            String label = "§7key: §f" + keyName;
            context.drawText(this.textRenderer, label, kbX + 8, kbY + 5, COLOR_WHITE, false);
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
    public boolean keyPressed(KeyInput input) {
        if (awaitingKeybindId != null) {
            int keyCode = input.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { awaitingKeybindId = null; return true; }
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                Config.setKeybind(awaitingKeybindId, -1);
                awaitingKeybindId = null;
                Config.save();
                return true;
            }
            Config.setKeybind(awaitingKeybindId, keyCode);
            awaitingKeybindId = null;
            Config.save();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (awaitingKeybindId != null) return false;
        if (activeTab >= 2) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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

        int tabsX = panelX + SIDEBAR_W + 16;
        int tx = tabsX;
        for (int i = 0; i < TABS.length; i++) {
            int tw = this.textRenderer.getWidth(TABS[i]) + 24;
            if (mx >= tx && mx <= tx + tw && my >= panelY + 8 && my <= panelY + 32) {
                activeTab = i;
                scrollOffset = 0;
                return true;
            }
            tx += tw + 8;
        }

        if (activeTab == 2) {
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
                    Config.theme = THEME_IDS[i];
                    Config.save();
                    return true;
                }
            }
            return true;
        }

        if (activeTab == 3) {
            int x0 = panelX + SIDEBAR_W + 30;
            int y0 = panelY + TAB_H + 45;
            int rowH = 70;
            int swatchSize = 22;
            int swatchGap = 6;
            for (int r = 0; r < 3; r++) {
                int ry = y0 + r * rowH;
                for (int c = 0; c < COLOR_OPTIONS.length; c++) {
                    int sx = x0 + c * (swatchSize + swatchGap);
                    int sy = ry;
                    if (mx >= sx && mx <= sx + swatchSize && my >= sy && my <= sy + swatchSize) {
                        int newCol = COLOR_OPTIONS[c];
                        if (r == 0) { Config.crosshairColor = newCol; Config.useCustomCrosshairColor = true; }
                        else if (r == 1) { Config.targetColor = newCol; Config.useCustomTargetColor = true; }
                        else if (r == 2) { Config.healthIndicatorColor = newCol; Config.useCustomHealthColor = true; }
                        Config.save();
                        return true;
                    }
                }

                int btnX = x0 + (swatchSize + swatchGap) * COLOR_OPTIONS.length + 10;
                int btnY = ry;
                int btnW = 80;
                int btnH = swatchSize;
                if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                    if (r == 0) Config.useCustomCrosshairColor = false;
                    else if (r == 1) Config.useCustomTargetColor = false;
                    else if (r == 2) Config.useCustomHealthColor = false;
                    Config.save();
                    return true;
                }
            }
            return true;
        }

        if (activeTab < TAB_MODULES.size()) {
            List<ModCard> cards = TAB_MODULES.get(activeTab);
            int cardsStartX = panelX + SIDEBAR_W + 16;
            int cardsStartY = panelY + TAB_H + 32;
            int cardsW = panelX + panelW - cardsStartX - 16;
            int cardW = (cardsW - CARD_GAP) / 2;
            int visibleTop = cardsStartY;
            int visibleBottom = panelY + panelH - 24;

            int col = 0, row = 0;
            for (ModCard card : cards) {
                int cx = cardsStartX + col * (cardW + CARD_GAP);
                int cy = cardsStartY + row * (CARD_H + CARD_GAP) - scrollOffset;
                if (my >= visibleTop && my <= visibleBottom
                        && mx >= cx && mx <= cx + cardW
                        && my >= cy && my <= cy + CARD_H) {

                    int kbY = cy + CARD_H - 26;
                    int kbH = 18;
                    int kbX = cx + 14;
                    int kbW = cardW - 28;
                    if (my >= kbY && my <= kbY + kbH && mx >= kbX && mx <= kbX + kbW) {
                        awaitingKeybindId = card.id;
                        return true;
                    }

                    boolean newVal = !card.getter.get();
                    card.setter.accept(newVal);
                    Config.save();
                    return true;
                }
                col++;
                if (col >= 2) { col = 0; row++; }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void close() { Config.save(); this.client.setScreen(null); }

    @Override
    public boolean shouldPause() { return false; }

    private static class ModCard {
        String id, title, description;
        java.util.function.Supplier<Boolean> getter;
        java.util.function.Consumer<Boolean> setter;
        ModCard(String id, String title, String desc, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
            this.id = id; this.title = title; this.description = desc;
            this.getter = getter; this.setter = setter;
        }
    }
}