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

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = this.client;

        context.fill(0, 0, this.width, this.height, 0x88000000);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§d§lHUD EDITOR").asOrderedText(),
                this.width / 2, 15, 0xFFA855F7);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§7Drag: move | Scroll: resize | ESC: save").asOrderedText(),
                this.width / 2, 30, 0xFF8A8A9C);

        if (mc.player == null) return;

        for (HudRenderer.HudElement el : HudRenderer.ELEMENTS) {
            if (!el.isEnabled()) continue;
            renderElement(mc, context, el, Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY()));
        }

        if (HudRenderer.keystrokesElement.isEnabled()) {
            Config.ElementPos pos = Config.getPos(
                    HudRenderer.keystrokesElement.getId(),
                    HudRenderer.keystrokesElement.getDefaultX(mc),
                    HudRenderer.keystrokesElement.getDefaultY(mc)
            );
            renderElement(mc, context, HudRenderer.keystrokesElement, pos);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderElement(MinecraftClient mc, DrawContext context, HudRenderer.HudElement el, Config.ElementPos pos) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) pos.x, (float) pos.y);
        context.getMatrices().scale(pos.scale, pos.scale);

        int w = el.getWidth(mc);
        int h = el.getHeight(mc);

        int borderColor = el.getId().equals(draggingId) ? 0xFF23A55A : 0xFFA855F7;
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

                // clamp — element bleibt im bild
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

                pos.x = newX;
                pos.y = newY;
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (draggingId != null) {
            draggingId = null;
            return true;
        }
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
            if (el.getId().equals(id)) {
                return Config.getPos(id, el.getDefaultX(), el.getDefaultY());
            }
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
    public void close() {
        Config.save();
        this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}