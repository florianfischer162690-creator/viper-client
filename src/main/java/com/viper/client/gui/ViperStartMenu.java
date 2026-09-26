package com.viper.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ViperStartMenu extends Screen {

    private static final int BTN_SIZE = 44;

    private static final int COLOR_ACCENT       = 0xFFA855F7;
    private static final int COLOR_ACCENT_DARK  = 0xFF7c3aed;
    private static final int COLOR_ACCENT_GLOW  = 0x55A855F7;
    private static final int COLOR_WHITE        = 0xFFFFFFFF;
    private static final int COLOR_MUTED        = 0xFF8A8A9C;

    private int menuX;
    private int menuY;
    private boolean hoveredLabel = false;
    private boolean hoveredBtn = false;

    // label rect wird im render berechnet und für hit-test zwischengespeichert
    private int labelX, labelY, labelW, labelH;

    public ViperStartMenu() {
        super(Text.literal("Viper V1 — Menu"));
    }

    @Override
    protected void init() {
        menuX = this.width / 2;
        menuY = this.height / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xBB0a0a12);
        drawSoftVignette(context);

        int cx = menuX;
        int cy = menuY;

        long t = System.currentTimeMillis();
        float pulse = (float) (Math.sin(t / 500.0) * 0.5 + 0.5);

        drawTitle(context, cx, cy - 80, pulse);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                "§7§lP V P   C L I E N T",
                cx, cy - 40, COLOR_MUTED
        );

        // ═══ MOD MENU LABEL — klickbar ═══
        labelW = 280;
        labelH = 26;
        labelX = cx - labelW / 2;
        labelY = cy - 5;

        hoveredLabel = mouseX >= labelX && mouseX <= labelX + labelW && mouseY >= labelY && mouseY <= labelY + labelH;

        // glow linie drüber
        context.fill(cx - 160, labelY - 12, cx + 160, labelY - 11, COLOR_ACCENT_GLOW);
        context.fill(cx - 160, labelY - 11, cx + 160, labelY - 10, COLOR_ACCENT);

        // box (hovered → heller + lila rahmen)
        int labelBg = hoveredLabel ? 0xFF2a1a44 : 0xCC1c1228;
        context.fill(labelX, labelY, labelX + labelW, labelY + labelH, labelBg);

        // rahmen oben/unten — immer lila, kräftiger beim hover
        int borderColor = hoveredLabel ? COLOR_ACCENT : COLOR_ACCENT_DARK;
        context.fill(labelX, labelY, labelX + labelW, labelY + 1, borderColor);
        context.fill(labelX, labelY + labelH - 1, labelX + labelW, labelY + labelH, borderColor);
        // rahmen links/rechts nur beim hover
        if (hoveredLabel) {
            context.fill(labelX, labelY, labelX + 1, labelY + labelH, borderColor);
            context.fill(labelX + labelW - 1, labelY, labelX + labelW, labelY + labelH, borderColor);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, "MOD MENU", cx, labelY + 9, COLOR_WHITE);

        // ═══ EIN BUTTON drunter (HUD-editor) ═══
        int btnY = labelY + labelH + 22;
        int bx = cx - BTN_SIZE / 2;

        hoveredBtn = mouseX >= bx && mouseX <= bx + BTN_SIZE && mouseY >= btnY && mouseY <= btnY + BTN_SIZE;

        int bg = hoveredBtn ? 0xFF2a1a44 : 0xCC1c1228;
        context.fill(bx, btnY, bx + BTN_SIZE, btnY + BTN_SIZE, bg);

        int bColor = hoveredBtn ? COLOR_ACCENT : 0x66a855f7;
        context.fill(bx, btnY, bx + BTN_SIZE, btnY + 1, bColor);
        context.fill(bx, btnY + BTN_SIZE - 1, bx + BTN_SIZE, btnY + BTN_SIZE, bColor);
        context.fill(bx, btnY, bx + 1, btnY + BTN_SIZE, bColor);
        context.fill(bx + BTN_SIZE - 1, btnY, bx + BTN_SIZE, btnY + BTN_SIZE, bColor);

        drawHudIcon(context, bx + BTN_SIZE / 2, btnY + BTN_SIZE / 2, hoveredBtn);

        if (hoveredBtn) {
            context.drawCenteredTextWithShadow(this.textRenderer, "HUD EDITOR", cx, btnY + BTN_SIZE + 10, COLOR_ACCENT);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTitle(DrawContext context, int cx, int cy, float pulse) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) cx, (float) cy);
        float scale = 2.6f + pulse * 0.05f;
        context.getMatrices().scale(scale, scale);

        String vip = "VIPER";
        String v1 = "V1";
        int vipW = this.textRenderer.getWidth(vip);
        int v1W = this.textRenderer.getWidth(v1);
        int logoW = 20;
        int gap = 10;
        int totalW = vipW + gap + logoW + gap + v1W;
        int startX = -totalW / 2;

        drawGlowText(context, "§l" + vip, startX, -4);
        drawVLogo(context, startX + vipW + gap, -8, logoW, 16, pulse);
        drawGlowText(context, "§l" + v1, startX + vipW + gap + logoW + gap, -4);

        context.getMatrices().popMatrix();
    }

    private void drawGlowText(DrawContext context, String txt, int x, int y) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                context.drawText(this.textRenderer, txt, x + dx, y + dy, COLOR_ACCENT, false);
            }
        }
        context.drawText(this.textRenderer, txt, x, y, COLOR_WHITE, false);
    }

    private void drawVLogo(DrawContext context, int x, int y, int w, int h, float pulse) {
        for (int i = 0; i < h; i++) {
            float t = (float) i / h;
            int lx = (int) (x + t * (w / 2 - 1));
            int rx = (int) (x + w - 2 - t * (w / 2 - 1));
            context.fill(lx - 1, y + i, lx + 3, y + i + 1, 0x55a855f7);
            context.fill(rx - 1, y + i, rx + 3, y + i + 1, 0x55a855f7);
        }
        for (int i = 0; i < h; i++) {
            float t = (float) i / h;
            int lx = (int) (x + t * (w / 2 - 1));
            int rx = (int) (x + w - 2 - t * (w / 2 - 1));
            context.fill(lx, y + i, lx + 2, y + i + 1, COLOR_ACCENT);
            context.fill(rx, y + i, rx + 2, y + i + 1, COLOR_ACCENT);
        }
        int sparkAlpha = (int) (pulse * 200 + 55);
        int sparkColor = (sparkAlpha << 24) | 0x00c084fc;
        context.fill(x - 3, y + h / 3, x - 2, y + h / 3 + 1, sparkColor);
        context.fill(x + w + 1, y + h / 2, x + w + 2, y + h / 2 + 1, sparkColor);
        context.fill(x + w / 2 - 1, y - 3, x + w / 2, y - 2, sparkColor);
    }

    private void drawHudIcon(DrawContext context, int cx, int cy, boolean hovered) {
        int color = hovered ? COLOR_ACCENT : COLOR_WHITE;
        int w = 18, h = 14;
        int x = cx - w / 2, y = cy - h / 2;

        context.fill(x, y, x + w, y + 2, color);
        context.fill(x, y + h - 2, x + w, y + h, color);
        context.fill(x, y, x + 2, y + h, color);
        context.fill(x + w - 2, y, x + w, y + h, color);
        context.fill(x + 3, y + 3, x + 6, y + h - 3, color);
        context.fill(x + 8, y + 4, x + w - 3, y + 6, color);
        context.fill(x + 8, y + 7, x + w - 3, y + 9, color);
        context.fill(x + 8, y + 10, x + w - 6, y + 12, color);
    }

    private void drawSoftVignette(DrawContext context) {
        int w = this.width;
        int h = this.height;
        int steps = 60;
        for (int i = 0; i < steps; i++) {
            int a = (int) ((1.0f - (float) i / steps) * 130);
            int color = 0x0a0214 | (a << 24);
            context.fill(i, 0, i + 1, h, color);
            context.fill(w - 1 - i, 0, w - i, h, color);
            context.fill(0, i, w, i + 1, color);
            context.fill(0, h - 1 - i, w, h - i, color);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        int mx = (int) click.x();
        int my = (int) click.y();

        // MOD MENU label → mods-screen
        if (mx >= labelX && mx <= labelX + labelW && my >= labelY && my <= labelY + labelH) {
            if (this.client != null) this.client.setScreen(new ViperModsScreen());
            return true;
        }

        // HUD button → hud-editor
        int btnY = labelY + labelH + 22;
        int bx = menuX - BTN_SIZE / 2;
        if (mx >= bx && mx <= bx + BTN_SIZE && my >= btnY && my <= btnY + BTN_SIZE) {
            if (this.client != null) this.client.setScreen(new HudEditorScreen());
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}