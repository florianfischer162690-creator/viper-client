package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryHudElement extends HudElement {

    private static final int SLOT_SIZE = 18;
    private static final int COLS = 9;
    private static final int ROWS = 4;
    private static final int BG_COLOR = 0xAA000000;

    @Override
    public String getId() { return "inventoryhud"; }

    @Override
    public boolean isEnabled() { return Config.showInventoryHud; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 200; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;
        int accent = Config.getAccent();
        PlayerInventory inv = mc.player.getInventory();

        int totalW = SLOT_SIZE * COLS;
        int totalH = SLOT_SIZE * ROWS;

        // hintergrund
        context.fill(x - 2, y - 2, x + totalW + 2, y + totalH + 2, BG_COLOR);
        // rand
        context.fill(x - 2, y - 2, x + totalW + 2, y - 1, accent);
        context.fill(x - 2, y + totalH + 1, x + totalW + 2, y + totalH + 2, accent);
        context.fill(x - 2, y - 2, x - 1, y + totalH + 2, accent);
        context.fill(x + totalW + 1, y - 2, x + totalW + 2, y + totalH + 2, accent);

        // items rendern — 27 = main (top 3 reihen), 0-8 = hotbar (bottom)
        // layout: row0-2 = slots 9..35 (main inventar, oben)
        //         row3   = slots 0..8  (hotbar, unten)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < COLS; col++) {
                int slotIndex = 9 + row * COLS + col; // 9..35
                ItemStack stack = inv.getStack(slotIndex);
                int sx = x + col * SLOT_SIZE;
                int sy = y + row * SLOT_SIZE;
                drawSlotBg(context, sx, sy, accent);
                if (!stack.isEmpty()) {
                    context.drawItem(stack, sx + 1, sy + 1);
                    context.drawStackOverlay(mc.textRenderer, stack, sx + 1, sy + 1);
                }
            }
        }
        // hotbar
        int hbY = y + 3 * SLOT_SIZE;
        for (int col = 0; col < COLS; col++) {
            ItemStack stack = inv.getStack(col); // 0..8
            int sx = x + col * SLOT_SIZE;
            int sy = hbY;
            // hotbar slots etwas anders gefärbt
            context.fill(sx, sy, sx + SLOT_SIZE, sy + SLOT_SIZE, 0x77000000);
            context.fill(sx, sy, sx + SLOT_SIZE, sy + 1, accent);
            context.fill(sx, sy + SLOT_SIZE - 1, sx + SLOT_SIZE, sy + SLOT_SIZE, accent);
            context.fill(sx, sy, sx + 1, sy + SLOT_SIZE, accent);
            context.fill(sx + SLOT_SIZE - 1, sy, sx + SLOT_SIZE, sy + SLOT_SIZE, accent);
            if (!stack.isEmpty()) {
                context.drawItem(stack, sx + 1, sy + 1);
                context.drawStackOverlay(mc.textRenderer, stack, sx + 1, sy + 1);
            }
        }

        return totalH + 4;
    }

    private void drawSlotBg(DrawContext context, int x, int y, int accent) {
        context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x55000000);
        context.fill(x, y, x + SLOT_SIZE, y + 1, 0x33000000);
        context.fill(x, y, x + 1, y + SLOT_SIZE, 0x33000000);
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return SLOT_SIZE * COLS + 4;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return SLOT_SIZE * ROWS + 4;
    }
}