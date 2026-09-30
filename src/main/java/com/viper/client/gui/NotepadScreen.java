package com.viper.client.gui;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NotepadScreen extends Screen {

    private static final int PANEL_MAX_W = 800;
    private static final int PANEL_MAX_H = 500;
    private static final int PADDING = 24;

    private static final int COLOR_BG     = 0xEE0a0a12;
    private static final int COLOR_PANEL  = 0xFF16121f;
    private static final int COLOR_TEXT   = 0xFFE6E6E6;
    private static final int COLOR_MUTED  = 0xFF8A8A9C;

    private int panelX, panelY, panelW, panelH;

    private final StringBuilder text = new StringBuilder();

    private long lastBlink = 0;
    private boolean cursorVisible = true;

    public NotepadScreen() {
        super(Text.literal("Viper V1 — Notepad"));
    }

    private static Path getNotepadFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("viper-notepad.txt");
    }

    private static String loadText() {
        try {
            Path p = getNotepadFile();
            if (Files.exists(p)) return Files.readString(p);
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Notepad] load failed", e);
        }
        return "";
    }

    private static void saveText(String t) {
        try {
            Files.writeString(getNotepadFile(), t);
        } catch (IOException e) {
            ViperClient.LOGGER.error("[Notepad] save failed", e);
        }
    }

    @Override
    protected void init() {
        panelW = Math.min(PANEL_MAX_W, this.width - 80);
        panelH = Math.min(PANEL_MAX_H, this.height - 80);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        text.setLength(0);
        text.append(loadText());
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

        context.drawText(this.textRenderer, "§l📓 NOTEPAD", panelX + PADDING, panelY + 16, accent, false);
        context.drawText(this.textRenderer, "§7Auto-saves on close · ESC to close",
                panelX + PADDING + 130, panelY + 16, COLOR_MUTED, false);

        int fieldX = panelX + PADDING - 2;
        int fieldY = panelY + 46;
        int fieldW = panelW - PADDING * 2 + 4;
        int fieldH = panelH - 46 - 22;
        context.fill(fieldX, fieldY, fieldX + fieldW, fieldY + fieldH, COLOR_PANEL);
        context.fill(fieldX, fieldY, fieldX + fieldW, fieldY + 1, accentDark);
        context.fill(fieldX, fieldY + fieldH - 1, fieldX + fieldW, fieldY + fieldH, accentDark);
        context.fill(fieldX, fieldY, fieldX + 1, fieldY + fieldH, accentDark);
        context.fill(fieldX + fieldW - 1, fieldY, fieldX + fieldW, fieldY + fieldH, accentDark);

        String fullText = text.toString();
        String[] lines = fullText.split("\n", -1);
        int lineH = this.textRenderer.fontHeight + 2;
        int maxLines = (fieldH - 8) / lineH;
        int startLine = Math.max(0, lines.length - maxLines);

        int ty = fieldY + 6;
        for (int i = startLine; i < lines.length; i++) {
            context.drawText(this.textRenderer, lines[i], fieldX + 6, ty, COLOR_TEXT, false);
            ty += lineH;
        }

        long now = System.currentTimeMillis();
        if (now - lastBlink > 500) {
            cursorVisible = !cursorVisible;
            lastBlink = now;
        }
        if (cursorVisible) {
            String lastLine = lines.length > 0 ? lines[lines.length - 1] : "";
            int lastLineIdx = lines.length - 1;
            int curY = fieldY + 6 + (lastLineIdx - startLine) * lineH;
            if (curY < fieldY + fieldH - 4) {
                int curX = fieldX + 6 + this.textRenderer.getWidth(lastLine);
                context.fill(curX, curY - 1, curX + 1, curY + this.textRenderer.fontHeight - 1, accent);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= 32) {
            text.append(chr);
            lastBlink = 0;
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (text.length() > 0) text.deleteCharAt(text.length() - 1);
            lastBlink = 0;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            text.append('\n');
            lastBlink = 0;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            text.append("    ");
            lastBlink = 0;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        try {
            saveText(text.toString());
            ViperClient.LOGGER.info("[Notepad] saved");
        } catch (Throwable t) {
            ViperClient.LOGGER.error("[Notepad] save on close failed", t);
        }
        if (this.client != null) this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() { return false; }
}