package com.viper.client.gui;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScreenshotPickerScreen extends Screen {

    private static final int PANEL_MAX_W = 720;
    private static final int PANEL_MAX_H = 460;
    private static final int ROW_H = 40;
    private static final int ROW_GAP = 4;

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_MUTED = 0xFF8A8A9C;
    private static final int COLOR_BG    = 0xEE0a0a12;
    private static final int COLOR_PANEL = 0xFF16121f;
    private static final int COLOR_ROW   = 0xFF1c1228;
    private static final int COLOR_ROW_H = 0xFF2a1a44;

    private int panelX, panelY, panelW, panelH;
    private final List<File> screenshots = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxScroll = 0;

    public ScreenshotPickerScreen() {
        super(Text.literal("Viper V1 — Screenshots"));
    }

    @Override
    protected void init() {
        panelW = Math.min(PANEL_MAX_W, this.width - 80);
        panelH = Math.min(PANEL_MAX_H, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        loadScreenshots();
    }

    private void loadScreenshots() {
        screenshots.clear();
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            File dir = new File(mc.runDirectory, "screenshots");
            if (!dir.exists()) return;
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null) return;

            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

            screenshots.addAll(Arrays.asList(files));
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();
        int accentDark = Config.getAccentDark();

        context.fill(0, 0, this.width, this.height, 0xBB000000);

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, COLOR_BG);
        context.fill(panelX, panelY, panelX + panelW, panelY + 1, accentDark);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, accentDark);
        context.fill(panelX, panelY, panelX + 1, panelY + panelH, accentDark);
        context.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, accentDark);

        context.drawText(this.textRenderer, "§l📸 SCREENSHOTS", panelX + 20, panelY + 16, accent, false);
        context.drawText(this.textRenderer, "§7" + screenshots.size() + " files — click to edit",
                panelX + 20, panelY + 30, COLOR_MUTED, false);

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + 20 && mouseY >= closeY && mouseY <= closeY + 20;
        if (closeHover) context.fill(closeX, closeY, closeX + 20, closeY + 20, 0xFF3a1a2a);
        context.drawText(this.textRenderer, "§c✕", closeX + 6, closeY + 6, 0xFFFF5555, false);

        context.fill(panelX + 1, panelY + 52, panelX + panelW - 1, panelY + 53, 0xFF2a1a3a);

        int listX = panelX + 16;
        int listY = panelY + 60;
        int listW = panelW - 32;
        int listBottom = panelY + panelH - 16;
        int visibleH = listBottom - listY;

        int totalH = screenshots.size() * (ROW_H + ROW_GAP);
        if (totalH > 0) totalH -= ROW_GAP;
        maxScroll = Math.max(0, totalH - visibleH);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        if (scrollOffset < 0) scrollOffset = 0;

        context.enableScissor(listX, listY, listX + listW, listBottom);

        if (screenshots.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    "§7No screenshots found", panelX + panelW / 2, panelY + panelH / 2, COLOR_MUTED);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            for (int i = 0; i < screenshots.size(); i++) {
                File f = screenshots.get(i);
                int ry = listY + i * (ROW_H + ROW_GAP) - scrollOffset;
                if (ry + ROW_H < listY || ry > listBottom) continue;

                boolean hovered = mouseX >= listX && mouseX <= listX + listW
                        && mouseY >= ry && mouseY <= ry + ROW_H
                        && mouseY >= listY && mouseY <= listBottom;

                context.fill(listX, ry, listX + listW, ry + ROW_H, hovered ? COLOR_ROW_H : COLOR_ROW);
                if (hovered) {
                    context.fill(listX, ry, listX + 3, ry + ROW_H, accent);
                }

                String name = f.getName();
                if (name.length() > 42) name = name.substring(0, 39) + "...";
                context.drawText(this.textRenderer, "§f" + name, listX + 12, ry + 8, COLOR_WHITE, false);

                String dateStr = sdf.format(new Date(f.lastModified()));
                long kb = f.length() / 1024;
                String sizeStr = kb + " KB";
                String meta = "§7" + dateStr + "  §8•  §7" + sizeStr;
                context.drawText(this.textRenderer, meta, listX + 12, ry + 22, COLOR_MUTED, false);

                if (hovered) {
                    int hintW = this.textRenderer.getWidth("§e▶ edit");
                    context.drawText(this.textRenderer, "§e▶ edit",
                            listX + listW - hintW - 12, ry + ROW_H / 2 - 4, 0xFFFFC107, false);
                }
            }
        }

        context.disableScissor();

        if (maxScroll > 0) {
            int barX = panelX + panelW - 8;
            int barY = listY;
            int barH = visibleH;
            context.fill(barX, barY, barX + 4, barY + barH, 0x44FFFFFF);
            float ratio = (float) visibleH / totalH;
            int thumbH = Math.max(20, (int) (barH * ratio));
            int thumbY = barY + (int) ((barH - thumbH) * ((float) scrollOffset / maxScroll));
            context.fill(barX, thumbY, barX + 4, thumbY + thumbH, accent);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int mx = (int) mouseX;
        int my = (int) mouseY;

        int closeX = panelX + panelW - 28;
        int closeY = panelY + 8;
        if (mx >= closeX && mx <= closeX + 20 && my >= closeY && my <= closeY + 20) {
            this.client.setScreen(null);
            return true;
        }

        int listX = panelX + 16;
        int listY = panelY + 60;
        int listW = panelW - 32;
        int listBottom = panelY + panelH - 16;

        for (int i = 0; i < screenshots.size(); i++) {
            int ry = listY + i * (ROW_H + ROW_GAP) - scrollOffset;
            if (ry + ROW_H < listY || ry > listBottom) continue;

            if (mx >= listX && mx <= listX + listW
                    && my >= ry && my <= ry + ROW_H
                    && my >= listY && my <= listBottom) {
                File selected = screenshots.get(i);
                if (this.client != null) {
                    this.client.setScreen(new ScreenshotEditorScreen(selected));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxScroll > 0) {
            scrollOffset -= (int) (verticalAmount * 20);
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > maxScroll) scrollOffset = maxScroll;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() { return false; }
}