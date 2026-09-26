package com.viper.client.gui;

import com.viper.client.config.Config;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ViperModsScreen extends Screen {

    private static final int SIDEBAR_W = 78;
    private static final int TAB_H = 28;
    private static final int CARD_H = 76;
    private static final int CARD_GAP = 8;

    private static final int COLOR_ACCENT      = 0xFFA855F7;
    private static final int COLOR_ACCENT_DARK = 0xFF7c3aed;
    private static final int COLOR_WHITE       = 0xFFFFFFFF;
    private static final int COLOR_MUTED       = 0xFF8A8A9C;
    private static final int COLOR_BG          = 0xEE0a0a12;
    private static final int COLOR_PANEL       = 0xFF16121f;
    private static final int COLOR_CARD        = 0xFF1c1228;
    private static final int COLOR_CARD_HOVER  = 0xFF2a1a44;
    private static final int COLOR_TOGGLE_OFF  = 0xFF3a3a4a;
    private static final int COLOR_TOGGLE_ON   = 0xFFA855F7;

    private int panelX, panelY, panelW, panelH;

    private int activeTab = 0;
    private final String[] TABS = {"HUD", "PVP"};

    private final List<List<ModCard>> TAB_MODULES = new ArrayList<>();

    private static final String[] SIDEBAR_LABELS = {"COSMETICS", "SKINS", "EMOTES", "FRIENDS"};

    public ViperModsScreen() {
        super(Text.literal("Viper V1 — Mods"));
    }

    @Override
    protected void init() {
        panelW = Math.min(720, this.width - 80);
        panelH = Math.min(440, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        TAB_MODULES.clear();

        List<ModCard> hud = new ArrayList<>();
        hud.add(new ModCard("fps", "FPS", "Show FPS counter", () -> Config.showFps, v -> Config.showFps = v));
        hud.add(new ModCard("cps", "CPS", "Show clicks per second", () -> Config.showCps, v -> Config.showCps = v));
        hud.add(new ModCard("coords", "COORDS", "Show XYZ position", () -> Config.showCoords, v -> Config.showCoords = v));
        hud.add(new ModCard("ping", "PING", "Show network ping", () -> Config.showPing, v -> Config.showPing = v));
        hud.add(new ModCard("armor", "ARMOR", "Show armor HUD", () -> Config.showArmor, v -> Config.showArmor = v));
        hud.add(new ModCard("potion", "POTIONS", "Show active effects", () -> Config.showPotion, v -> Config.showPotion = v));
        hud.add(new ModCard("keystrokes", "KEYSTROKES", "Show WASD keys", () -> Config.showKeystrokes, v -> Config.showKeystrokes = v));
        hud.add(new ModCard("hudtoggle", "HUD MASTER", "Toggle entire HUD (F4)", () -> Config.hudEnabled, v -> Config.hudEnabled = v));
        TAB_MODULES.add(hud);

        List<ModCard> pvp = new ArrayList<>();
        pvp.add(new ModCard("togglesprint", "TOGGLE SPRINT", "Auto-sprint toggle (R)", () -> Config.toggleSprint, v -> Config.toggleSprint = v));
        pvp.add(new ModCard("customcrosshair", "CUSTOM CROSSHAIR", "Lila crosshair", () -> Config.showCustomCrosshair, v -> Config.showCustomCrosshair = v));
        pvp.add(new ModCard("targetindicator", "TARGET INDICATOR", "Box when aiming at entity", () -> Config.showTargetIndicator, v -> Config.showTargetIndicator = v));
        TAB_MODULES.add(pvp);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xBB000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, COLOR_BG);

        context.fill(panelX, panelY, panelX + panelW, panelY + 1, COLOR_ACCENT_DARK);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, COLOR_ACCENT_DARK);
        context.fill(panelX, panelY, panelX + 1, panelY + panelH, COLOR_ACCENT_DARK);
        context.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, COLOR_ACCENT_DARK);

        context.fill(panelX, panelY, panelX + SIDEBAR_W, panelY + panelH, COLOR_PANEL);
        context.fill(panelX + SIDEBAR_W, panelY, panelX + SIDEBAR_W + 1, panelY + panelH, COLOR_ACCENT_DARK);

        drawSidebar(context, mouseX, mouseY);

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
                context.fill(tx, panelY + 32, tx + tw, panelY + 33, COLOR_ACCENT);
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

        if (activeTab < TAB_MODULES.size()) {
            List<ModCard> cards = TAB_MODULES.get(activeTab);
            int cardsStartX = panelX + SIDEBAR_W + 16;
            int cardsStartY = panelY + TAB_H + 32;
            int cardsW = panelX + panelW - cardsStartX - 16;
            int cardW = (cardsW - CARD_GAP) / 2;

            int col = 0, row = 0;
            for (ModCard card : cards) {
                int cx = cardsStartX + col * (cardW + CARD_GAP);
                int cy = cardsStartY + row * (CARD_H + CARD_GAP);
                if (cy + CARD_H > panelY + panelH - 24) break;

                boolean hovered = mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + CARD_H;
                drawCard(context, card, cx, cy, cardW, CARD_H, hovered);

                col++;
                if (col >= 2) { col = 0; row++; }
            }
        }

        context.drawText(this.textRenderer, "§7Click a card to toggle | ESC to close",
                panelX + SIDEBAR_W + 16, panelY + panelH - 16, COLOR_MUTED, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY) {
        int iconSize = 40;
        int labelH = 12;
        int itemH = iconSize + labelH + 6;
        int gap = 8;
        int startY = panelY + 20;
        int cx = panelX + SIDEBAR_W / 2;

        {
            int iy = startY;
            int bg = 0xFF2a1a44;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, bg);
            context.fill(cx - iconSize / 2, iy, cx - iconSize / 2 + 2, iy + iconSize, COLOR_ACCENT);
            drawModsIcon(context, cx, iy + iconSize / 2);
            context.drawCenteredTextWithShadow(this.textRenderer, "MODS", cx, iy + iconSize + 2, COLOR_ACCENT);
        }

        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            boolean hovered = mouseX >= cx - iconSize / 2 && mouseX <= cx + iconSize / 2 && mouseY >= iy && mouseY <= iy + iconSize;
            int bg = hovered ? 0x33A855F7 : 0x00000000;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, bg);
            drawPlaceholderIcon(context, i, cx, iy + iconSize / 2, hovered);
            context.drawCenteredTextWithShadow(this.textRenderer, SIDEBAR_LABELS[i], cx, iy + iconSize + 2, hovered ? COLOR_ACCENT : COLOR_MUTED);
        }
    }

    private void drawModsIcon(DrawContext context, int cx, int cy) {
        context.fill(cx - 6, cy - 6, cx + 6, cy + 6, COLOR_ACCENT);
        context.fill(cx - 3, cy - 3, cx + 3, cy + 3, 0xFF16121f);
    }

    private void drawPlaceholderIcon(DrawContext context, int index, int cx, int cy, boolean hovered) {
        int color = hovered ? COLOR_ACCENT : COLOR_MUTED;
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

    private void drawCard(DrawContext context, ModCard card, int x, int y, int w, int h, boolean hovered) {
        int bg = hovered ? COLOR_CARD_HOVER : COLOR_CARD;
        context.fill(x, y, x + w, y + h, bg);

        if (card.getter.get()) {
            context.fill(x, y, x + 3, y + h, COLOR_ACCENT);
        }

        if (hovered) {
            context.fill(x, y, x + w, y + 1, COLOR_ACCENT);
            context.fill(x, y + h - 1, x + w, y + h, COLOR_ACCENT);
            context.fill(x + w - 1, y, x + w, y + h, COLOR_ACCENT);
        }

        int iconX = x + 14;
        int iconY = y + 14;
        context.fill(iconX, iconY, iconX + 12, iconY + 12, card.getter.get() ? COLOR_ACCENT : COLOR_MUTED);

        context.drawText(this.textRenderer, "§l" + card.title, x + 36, y + 12, COLOR_WHITE, false);
        context.drawText(this.textRenderer, "§7" + card.description, x + 36, y + 28, COLOR_MUTED, false);

        int tw = 24, th = 12;
        int tx = x + w - tw - 14;
        int ty = y + h - th - 14;
        context.fill(tx, ty, tx + tw, ty + th, card.getter.get() ? COLOR_TOGGLE_ON : COLOR_TOGGLE_OFF);

        int dotX = card.getter.get() ? tx + tw - 10 : tx + 2;
        context.fill(dotX, ty + 2, dotX + 8, ty + th - 2, COLOR_WHITE);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);

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
                return true;
            }
            tx += tw + 8;
        }

        if (activeTab < TAB_MODULES.size()) {
            List<ModCard> cards = TAB_MODULES.get(activeTab);
            int cardsStartX = panelX + SIDEBAR_W + 16;
            int cardsStartY = panelY + TAB_H + 32;
            int cardsW = panelX + panelW - cardsStartX - 16;
            int cardW = (cardsW - CARD_GAP) / 2;

            int col = 0, row = 0;
            for (ModCard card : cards) {
                int cx = cardsStartX + col * (cardW + CARD_GAP);
                int cy = cardsStartY + row * (CARD_H + CARD_GAP);
                if (cy + CARD_H > panelY + panelH - 24) break;

                if (mx >= cx && mx <= cx + cardW && my >= cy && my <= cy + CARD_H) {
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
    public void close() {
        Config.save();
        this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class ModCard {
        String id, title, description;
        java.util.function.Supplier<Boolean> getter;
        java.util.function.Consumer<Boolean> setter;

        ModCard(String id, String title, String desc, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
            this.id = id;
            this.title = title;
            this.description = desc;
            this.getter = getter;
            this.setter = setter;
        }
    }
}