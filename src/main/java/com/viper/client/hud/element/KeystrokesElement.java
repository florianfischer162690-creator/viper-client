package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class KeystrokesElement extends HudElement {

    private static final int KEY_SIZE = 18;
    private static final int GAP = 2;

    private static final int COLOR_BG_OFF   = 0x88000000;
    private static final int COLOR_BG_ON    = 0xFFA855F7;
    private static final int COLOR_BORDER   = 0xFFA855F7;
    private static final int COLOR_TEXT_OFF = 0xFF8A8A9C;
    private static final int COLOR_TEXT_ON  = 0xFFFFFFFF;

    @Override
    public String getId() { return "keystrokes"; }

    @Override
    public boolean isEnabled() {
        return Config.showKeystrokes;
    }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 4; }

    public int getDefaultX(MinecraftClient mc) {
        return 4;
    }

    public int getDefaultY(MinecraftClient mc) {
        return mc.getWindow().getScaledHeight() - 70;
    }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        long window = mc.getWindow().getHandle();

        boolean w = isPressed(window, GLFW.GLFW_KEY_W);
        boolean a = isPressed(window, GLFW.GLFW_KEY_A);
        boolean s = isPressed(window, GLFW.GLFW_KEY_S);
        boolean d = isPressed(window, GLFW.GLFW_KEY_D);
        boolean space = isPressed(window, GLFW.GLFW_KEY_SPACE);

        int totalWidth = KEY_SIZE * 3 + GAP * 2;
        int totalHeight = KEY_SIZE * 2 + GAP + GAP + 12 + GAP;

        int wX = x + KEY_SIZE + GAP;
        int wY = y;
        drawKey(context, mc, wX, wY, "W", w);

        int asdY = y + KEY_SIZE + GAP;
        drawKey(context, mc, x, asdY, "A", a);
        drawKey(context, mc, x + KEY_SIZE + GAP, asdY, "S", s);
        drawKey(context, mc, x + (KEY_SIZE + GAP) * 2, asdY, "D", d);

        int spaceY = asdY + KEY_SIZE + GAP + 2;
        int spaceW = totalWidth;
        int spaceH = 10;
        int bg = space ? COLOR_BG_ON : COLOR_BG_OFF;
        int txt = space ? COLOR_TEXT_ON : COLOR_TEXT_OFF;

        context.fill(x, spaceY, x + spaceW, spaceY + spaceH, bg);
        drawBorder(context, x, spaceY, spaceW, spaceH);
        context.drawCenteredTextWithShadow(mc.textRenderer, "SPACE", x + spaceW / 2, spaceY + 1, txt);

        return totalHeight;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return KEY_SIZE * 3 + GAP * 2;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return KEY_SIZE * 2 + GAP + GAP + 12 + GAP;
    }

    private void drawKey(DrawContext context, MinecraftClient mc, int x, int y, String label, boolean pressed) {
        int bg = pressed ? COLOR_BG_ON : COLOR_BG_OFF;
        int txt = pressed ? COLOR_TEXT_ON : COLOR_TEXT_OFF;

        context.fill(x, y, x + KEY_SIZE, y + KEY_SIZE, bg);
        drawBorder(context, x, y, KEY_SIZE, KEY_SIZE);
        context.drawCenteredTextWithShadow(mc.textRenderer, label, x + KEY_SIZE / 2, y + (KEY_SIZE - 8) / 2, txt);
    }

    private void drawBorder(DrawContext context, int x, int y, int w, int h) {
        context.fill(x, y, x + w, y + 1, COLOR_BORDER);
        context.fill(x, y + h - 1, x + w, y + h, COLOR_BORDER);
        context.fill(x, y, x + 1, y + h, COLOR_BORDER);
        context.fill(x + w - 1, y, x + w, y + h, COLOR_BORDER);
    }

    private boolean isPressed(long window, int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}