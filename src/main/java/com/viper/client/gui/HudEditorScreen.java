package com.viper.client.gui;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends Screen {

    private String draggingId = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudEditorScreen() { super(Text.literal("HUD Editor")); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = Config.getAccent();

        MinecraftClient mc = this.client;
        context.fill(0, 0, this.width, this.height, 0x88000000);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§lHUD EDITOR").asOrderedText(),
                this.width / 2, 15, accent);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§7Drag: move | Scroll: resize | ESC: save").asOrderedText(),
                this.width / 2, 30, 0xFF8A8A9C);

        if (mc.player == null) return;

        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (!el.isEnabled()) continue;
            Config.ElementPos pos = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());
            renderElementWithBorder(mc, context, el, pos, accent);
        }

        if (HudRenderer.keystrokesElement.isEnabled()) {
            Config.ElementPos pos = Config.getPos(
                    HudRenderer.keystrokesElement.getId(),
                    HudRenderer.keystrokesElement.getDefaultX(mc),
                    HudRenderer.keystrokesElement.getDefaultY(mc)
            );
            renderElementWithBorder(mc, context, HudRenderer.keystrokesElement, pos, accent);
        }

        // armor-element (fix position, zeigt nur rand)
        if (Config.showArmor && HudRenderer.armorElement.isEnabled()) {
            int scaledW = mc.getWindow().getScaledWidth();
            int scaledH = mc.getWindow().getScaledHeight();
            int armorW = HudRenderer.armorElement.getWidth(mc);
            int armorH = HudRenderer.armorElement.getHeight(mc);
            int ax = (scaledW / 2) - 91 - 29 - armorW - 6;
            int ay = (scaledH - 11) - (armorH / 2);

            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) ax, (float) ay);

            context.fill(-1, -1, armorW + 1, 0, accent);
            context.fill(-1, armorH, armorW + 1, armorH + 1, accent);
            context.fill(-1, -1, 0, armorH + 1, accent);
            context.fill(armorW, -1, armorW + 1, armorH + 1, accent);

            HudRenderer.armorElement.render(context, mc, 0, 0);
            context.getMatrices().popMatrix();
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderElementWithBorder(MinecraftClient mc, DrawContext context, HudRenderer.HudElement el, Config.ElementPos pos, int accent) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) pos.x, (float) pos.y);
        context.getMatrices().scale(pos.scale, pos.scale);

        int w = el.getWidth(mc);
        int h = el.getHeight(mc);

        int borderColor = el.getId().equals(draggingId) ? 0xFF23A55A : accent;
        context.fill(-1, -1, w + 1, 0, borderColor);
        context.fill(-1, h, w + 1, h + 1, borderColor);
        context.fill(-1, -1, 0, h + 1, borderColor);
        context.fill(w, -1, w + 1, h + 1, borderColor);

        el.render(context, mc, 0, 0);
        context.getMatrices().popMatrix();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        int mx = (int) click.x();
        int my = (int) click.y();
        MinecraftClient mc = this.client;

        Config.ElementPos kpos = Config.getPos(
                HudRenderer.keystrokesElement.getId(),
                HudRenderer.keystrokesElement.getDefaultX(mc),
                HudRenderer.keystrokesElement.getDefaultY(mc)
        );
        if (hitTest(mc, HudRenderer.keystrokesElement, kpos, mx, my)) {
            draggingId = HudRenderer.keystrokesElement.getId();
            dragOffsetX = mx - kpos.x;
            dragOffsetY = my - kpos.y;
            return true;
        }

        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (!el.isEnabled()) continue;
            Config.ElementPos pos = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());
            if (hitTest(mc, el, pos, mx, my)) {
                draggingId = el.getId();
                dragOffsetX = mx - pos.x;
                dragOffsetY = my - pos.y;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (draggingId != null) {
            Config.ElementPos pos = findPos(draggingId);
            if (pos != null) {
                int newX = (int) click.x() - dragOffsetX;
                int newY = (int) click.y() - dragOffsetY;

                MinecraftClient mc = this.client;
                HudRenderer.HudElement el = findElement(draggingId);
                int w = el != null ? (int) (el.getWidth(mc) * pos.scale) : 40;
                int h = el != null ? (int) (el.getHeight(mc) * pos.scale) : 20;

                int maxX = this.width - w;
                int maxY = this.height - h;
                if (newX < 0) newX = 0;
                if (newY < 0) newY = 0;
                if (newX > maxX) newX = maxX;
                if (newY > maxY) newY = maxY;

                if (!overlaps(mc, draggingId, newX, newY, w, h)) {
                    pos.x = newX;
                    pos.y = newY;
                }
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    private boolean overlaps(MinecraftClient mc, String selfId, int nx, int ny, int w, int h) {
        if (!selfId.equals(HudRenderer.keystrokesElement.getId())) {
            Config.ElementPos kpos = Config.getPos(
                    HudRenderer.keystrokesElement.getId(),
                    HudRenderer.keystrokesElement.getDefaultX(mc),
                    HudRenderer.keystrokesElement.getDefaultY(mc)
            );
            int kw = (int) (HudRenderer.keystrokesElement.getWidth(mc) * kpos.scale);
            int kh = (int) (HudRenderer.keystrokesElement.getHeight(mc) * kpos.scale);
            if (rectIntersect(nx, ny, w, h, kpos.x, kpos.y, kw, kh)) return true;
        }

        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (el.getId().equals(selfId)) continue;
            if (!el.isEnabled()) continue;
            Config.ElementPos p = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());
            int ew = (int) (el.getWidth(mc) * p.scale);
            int eh = (int) (el.getHeight(mc) * p.scale);
            if (rectIntersect(nx, ny, w, h, p.x, p.y, ew, eh)) return true;
        }
        return false;
    }

    private boolean rectIntersect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (draggingId != null) { draggingId = null; return true; }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        MinecraftClient mc = this.client;

        Config.ElementPos kpos = Config.getPos(
                HudRenderer.keystrokesElement.getId(),
                HudRenderer.keystrokesElement.getDefaultX(mc),
                HudRenderer.keystrokesElement.getDefaultY(mc)
        );
        if (hitTest(mc, HudRenderer.keystrokesElement, kpos, (int) mouseX, (int) mouseY)) {
            changeScale(kpos, verticalAmount);
            return true;
        }

        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (!el.isEnabled()) continue;
            Config.ElementPos pos = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());
            if (hitTest(mc, el, pos, (int) mouseX, (int) mouseY)) {
                changeScale(pos, verticalAmount);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void changeScale(Config.ElementPos pos, double amount) {
        pos.scale += (float) amount * 0.1f;
        if (pos.scale < 0.5f) pos.scale = 0.5f;
        if (pos.scale > 3.0f) pos.scale = 3.0f;
    }

    private boolean hitTest(MinecraftClient mc, HudRenderer.HudElement el, Config.ElementPos pos, int mx, int my) {
        int w = (int) (el.getWidth(mc) * pos.scale);
        int h = (int) (el.getHeight(mc) * pos.scale);
        return mx >= pos.x && mx <= pos.x + w && my >= pos.y && my <= pos.y + h;
    }

    private Config.ElementPos findPos(String id) {
        MinecraftClient mc = this.client;
        if (id.equals(HudRenderer.keystrokesElement.getId())) {
            return Config.getPos(id, HudRenderer.keystrokesElement.getDefaultX(mc), HudRenderer.keystrokesElement.getDefaultY(mc));
        }
        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (el.getId().equals(id)) return Config.getPos(id, el.getDefaultX(), el.getDefaultY());
        }
        return null;
    }

    private HudRenderer.HudElement findElement(String id) {
        if (id.equals(HudRenderer.keystrokesElement.getId())) return HudRenderer.keystrokesElement;
        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (el.getId().equals(id)) return el;
        }
        return null;
    }

    @Override
    public void close() { Config.save(); this.client.setScreen(null); }

    @Override
    public boolean shouldPause() { return false; }
}