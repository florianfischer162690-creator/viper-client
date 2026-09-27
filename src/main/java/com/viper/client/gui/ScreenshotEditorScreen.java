package com.viper.client.gui;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ScreenshotEditorScreen extends Screen {

    private final File screenshotFile;

    private BufferedImage previewImage;
    private boolean loaded = false;
    private String loadError = null;

    private Identifier textureId = null;
    private int textureW = 0;
    private int textureH = 0;

    private int btnBaseX;
    private int btnBaseY;
    private int btnW = 200;
    private int btnH = 32;
    private int btnGap = 10;

    private String status = "";
    private long statusTime = 0;

    public ScreenshotEditorScreen(File screenshotFile) {
        super(Text.literal("Screenshot Editor"));
        this.screenshotFile = screenshotFile;
    }

    @Override
    protected void init() {
        btnBaseX = this.width / 2 - btnW / 2;
        btnBaseY = this.height - 220;

        loadPreview();
    }

    private void loadPreview() {
        try {
            previewImage = ImageIO.read(screenshotFile);
            if (previewImage == null) {
                loadError = "Could not read file";
                loaded = false;
                return;
            }
            loaded = true;

            try (InputStream in = new FileInputStream(screenshotFile)) {
                NativeImage nativeImage = NativeImage.read(in);
                textureW = nativeImage.getWidth();
                textureH = nativeImage.getHeight();

                String uniqueName = "screenshot_preview_" + screenshotFile.hashCode();
                final String texName = uniqueName;
                textureId = Identifier.of("viper", uniqueName.toLowerCase().replaceAll("[^a-z0-9_]", "_"));

                NativeImageBackedTexture tex = new NativeImageBackedTexture(() -> texName, nativeImage);
                MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, tex);
            }
        } catch (Exception e) {
            ViperClient.LOGGER.error("[ScreenshotEditor] load failed", e);
            loadError = e.getMessage();
            loaded = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();
        int accentDark = Config.getAccentDark();

        context.fill(0, 0, this.width, this.height, 0xDD0a0a12);

        context.drawCenteredTextWithShadow(this.textRenderer, "§l📸 SCREENSHOT EDITOR", this.width / 2, 20, accent);
        context.drawCenteredTextWithShadow(this.textRenderer, "§7Choose how to save: " + screenshotFile.getName(), this.width / 2, 40, 0xFF8A8A9C);

        int previewMaxW = 500;
        int previewMaxH = 280;
        int previewW = previewMaxW;
        int previewH = previewMaxH;
        if (textureW > 0 && textureH > 0) {
            double ratio = (double) textureW / textureH;
            double boxRatio = (double) previewMaxW / previewMaxH;
            if (ratio > boxRatio) {
                previewW = previewMaxW;
                previewH = (int) (previewMaxW / ratio);
            } else {
                previewH = previewMaxH;
                previewW = (int) (previewMaxH * ratio);
            }
        }
        int previewX = this.width / 2 - previewW / 2;
        int previewY = 70;

        context.fill(previewX - 2, previewY - 2, previewX + previewW + 2, previewY + previewH + 2, accent);

        if (!loaded) {
            context.fill(previewX, previewY, previewX + previewW, previewY + previewH, 0xFF000000);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7Loading...", this.width / 2, previewY + previewH / 2, 0xFF8A8A9C);
        } else if (textureId == null) {
            context.fill(previewX, previewY, previewX + previewW, previewY + previewH, 0xFF000000);
            context.drawCenteredTextWithShadow(this.textRenderer, "§c✗ texture load failed", this.width / 2, previewY + previewH / 2, 0xFFFF5555);
        } else {
            try {
                context.drawTexture(
                        net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
                        textureId,
                        previewX, previewY,
                        previewW, previewH,
                        0, 0,
                        textureW, textureH,
                        textureW, textureH
                );
            } catch (Throwable t) {
                ViperClient.LOGGER.error("[ScreenshotEditor] draw failed", t);
                context.fill(previewX, previewY, previewX + previewW, previewY + previewH, 0xFF000000);
                context.drawCenteredTextWithShadow(this.textRenderer, "§c✗ draw failed", this.width / 2, previewY + previewH / 2, 0xFFFF5555);
            }
        }

        int y = btnBaseY;

        drawButton(context, btnBaseX, y, btnW, btnH, "🎨 SAVE WITH FRAME", mouseX, mouseY, accent, accentDark);
        y += btnH + btnGap;

        drawButton(context, btnBaseX, y, btnW, btnH, "📄 SAVE PLAIN", mouseX, mouseY, accent, accentDark);
        y += btnH + btnGap;

        drawButton(context, btnBaseX, y, btnW, btnH, "🗑 DELETE SCREENSHOT", mouseX, mouseY, 0xFFEF4444, 0xFFDC2626);
        y += btnH + btnGap;

        if (!status.isEmpty() && System.currentTimeMillis() - statusTime < 3000) {
            context.drawCenteredTextWithShadow(this.textRenderer, status, this.width / 2, y + 10, accent);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, "§7ESC to save plain and close", this.width / 2, this.height - 20, 0xFF8A8A9C);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawButton(DrawContext context, int x, int y, int w, int h, String label, int mouseX, int mouseY, int accent, int accentDark) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = hovered ? Config.getAccentBg() : 0xFF16121f;
        int border = hovered ? accent : accentDark;

        context.fill(x, y, x + w, y + h, bg);
        context.fill(x, y, x + w, y + 1, border);
        context.fill(x, y + h - 1, x + w, y + h, border);
        context.fill(x, y, x + 1, y + h, border);
        context.fill(x + w - 1, y, x + w, y + h, border);

        context.drawCenteredTextWithShadow(this.textRenderer, label, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        int mx = (int) click.x();
        int my = (int) click.y();

        int y = btnBaseY;

        if (mx >= btnBaseX && mx <= btnBaseX + btnW && my >= y && my <= y + btnH) {
            saveWithFrame();
            return true;
        }
        y += btnH + btnGap;

        if (mx >= btnBaseX && mx <= btnBaseX + btnW && my >= y && my <= y + btnH) {
            status = "§a✓ Saved as-is";
            statusTime = System.currentTimeMillis();
            close();
            return true;
        }
        y += btnH + btnGap;

        if (mx >= btnBaseX && mx <= btnBaseX + btnW && my >= y && my <= y + btnH) {
            if (screenshotFile.exists()) screenshotFile.delete();
            status = "§c✗ Deleted";
            statusTime = System.currentTimeMillis();
            close();
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    private void saveWithFrame() {
        try {
            if (previewImage == null) {
                status = "§c✗ Image not loaded";
                statusTime = System.currentTimeMillis();
                return;
            }
            int accent = Config.getAccent();

            int border = 8;
            int newW = previewImage.getWidth() + border * 2;
            int newH = previewImage.getHeight() + border * 2;

            BufferedImage framed = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = framed.createGraphics();
            g.setColor(new java.awt.Color(accent, true));
            g.fillRect(0, 0, newW, newH);
            g.drawImage(previewImage, border, border, null);
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16));
            g.drawString("VIPER V1", border + 10, newH - border - 10);
            g.dispose();

            String origName = screenshotFile.getName();
            String newName = origName.replace(".png", "_framed.png");
            File framedFile = new File(screenshotFile.getParentFile(), newName);
            ImageIO.write(framed, "png", framedFile);

            status = "§a✓ Saved as " + newName;
            statusTime = System.currentTimeMillis();

            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                if (this.client != null) this.client.execute(this::close);
            }).start();
        } catch (Exception e) {
            status = "§c✗ Error: " + e.getMessage();
            statusTime = System.currentTimeMillis();
        }
    }

    @Override
    public void close() {
        try {
            if (textureId != null) {
                MinecraftClient.getInstance().getTextureManager().destroyTexture(textureId);
                textureId = null;
            }
        } catch (Throwable ignored) {}
        if (this.client != null) this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() { return false; }
}