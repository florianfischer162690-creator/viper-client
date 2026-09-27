package com.viper.client.gui;

import com.viper.client.config.Config;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ViperPlaceholderScreen extends Screen {

    private static final int SIDEBAR_W = 78;
    private static final int TAB_H = 28;

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_MUTED = 0xFF8A8A9C;
    private static final int COLOR_BG    = 0xEE0a0a12;
    private static final int COLOR_PANEL = 0xFF16121f;

    private static final String[] SIDEBAR_LABELS = {"COSMETICS", "SKINS", "EMOTES", "FRIENDS"};

    private int panelX, panelY, panelW, panelH;
    private final int categoryIndex;

    public ViperPlaceholderScreen(int categoryIndex) {
        super(Text.literal("Viper V1 — " + (categoryIndex >= 0 && categoryIndex < SIDEBAR_LABELS.length ? SIDEBAR_LABELS[categoryIndex] : "Category")));
        this.categoryIndex = categoryIndex;
    }

    @Override
    protected void init() {
        panelW = Math.min(720, this.width - 80);
        panelH = Math.min(440, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();
        int accentLight = Config.getAccentLight();
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

        String title = SIDEBAR_LABELS[categoryIndex];
        context.drawText(this.textRenderer, "§l" + title, panelX + SIDEBAR_W + 20, panelY + 16, COLOR_WHITE, false);
        context.fill(panelX + SIDEBAR_W + 20, panelY + 30, panelX + SIDEBAR_W + 20 + this.textRenderer.getWidth(title) + 8, panelY + 32, accent);

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 20 && mouseY >= closeY && mouseY <= closeY + 20;
        if (closeHover) context.fill(closeX, closeY, closeX + 20, closeY + 20, 0xFF3a1a2a);
        context.drawText(this.textRenderer, "§c✕", closeX + 6, closeY + 6, 0xFFFF5555, false);

        context.fill(panelX + SIDEBAR_W + 1, panelY + TAB_H + 18, panelX + panelW, panelY + TAB_H + 19, 0xFF2a1a3a);

        int cx = panelX + SIDEBAR_W + (panelW - SIDEBAR_W) / 2;
        int cy = panelY + panelH / 2;

        context.drawCenteredTextWithShadow(this.textRenderer, "§lComing Soon", cx, cy - 10, accent);
        context.drawCenteredTextWithShadow(this.textRenderer, "§7This feature is not available yet", cx, cy + 10, COLOR_MUTED);

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
            boolean hovered = mouseX >= cx - iconSize / 2 && mouseX <= cx + iconSize / 2 && mouseY >= iy && mouseY <= iy + iconSize;
            int bg = hovered ? accentHover : 0x00000000;
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, bg);
            drawModsIcon(context, cx, iy + iconSize / 2, hovered ? accent : COLOR_MUTED);
            context.drawCenteredTextWithShadow(this.textRenderer, "MODS", cx, iy + iconSize + 2, hovered ? accent : COLOR_MUTED);
        }

        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            boolean active = (i == categoryIndex);
            boolean hovered = mouseX >= cx - iconSize / 2 && mouseX <= cx + iconSize / 2 && mouseY >= iy && mouseY <= iy + iconSize;

            int bg = active ? accentBg : (hovered ? accentHover : 0x00000000);
            context.fill(cx - iconSize / 2, iy, cx + iconSize / 2, iy + iconSize, bg);
            if (active) context.fill(cx - iconSize / 2, iy, cx - iconSize / 2 + 2, iy + iconSize, accent);

            int iconColor = (active || hovered) ? accent : COLOR_MUTED;
            drawPlaceholderIcon(context, i, cx, iy + iconSize / 2, iconColor);
            context.drawCenteredTextWithShadow(this.textRenderer, SIDEBAR_LABELS[i], cx, iy + iconSize + 2, (active || hovered) ? accent : COLOR_MUTED);
        }
    }

    private void drawModsIcon(DrawContext context, int cx, int cy, int color) {
        context.fill(cx - 6, cy - 6, cx + 6, cy + 6, color);
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

        if (mx >= cxSide - iconSize / 2 && mx <= cxSide + iconSize / 2 && my >= startY && my <= startY + itemH) {
            if (this.client != null) this.client.setScreen(new ViperModsScreen());
            return true;
        }

        int startY2 = startY + itemH + gap;
        for (int i = 0; i < SIDEBAR_LABELS.length; i++) {
            int iy = startY2 + i * (itemH + gap);
            if (mx >= cxSide - iconSize / 2 && mx <= cxSide + iconSize / 2 && my >= iy && my <= iy + itemH) {
                if (i != categoryIndex && this.client != null) {
                    this.client.setScreen(new ViperPlaceholderScreen(i));
                }
                return true;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void close() { this.client.setScreen(null); }

    @Override
    public boolean shouldPause() { return false; }
}