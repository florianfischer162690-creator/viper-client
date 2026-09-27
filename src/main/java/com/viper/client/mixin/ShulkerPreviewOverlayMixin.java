package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(HandledScreen.class)
public abstract class ShulkerPreviewOverlayMixin {

    @Shadow public abstract Slot getFocusedSlot();

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void viper_shulkerOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            if (!Config.showShulkerPreview) return;
            Slot focusedSlot = this.getFocusedSlot();
            if (focusedSlot == null) return;
            ItemStack stack = focusedSlot.getStack();
            if (stack == null || stack.isEmpty()) return;
            ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
            if (container == null) return;
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack s : container.iterateNonEmpty()) items.add(s);
            if (items.isEmpty()) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            TextRenderer textRenderer = mc.textRenderer;
            int accent = Config.getAccent();

            int cols = 9, rows = 3, slot = 18, padding = 6, titleH = 12;
            int panelW = cols * slot + padding * 2;
            int panelH = rows * slot + padding * 2 + titleH;

            int px = mouseX + 12;
            int py = mouseY - 12;
            int screenW = context.getScaledWindowWidth();
            int screenH = context.getScaledWindowHeight();
            if (px + panelW > screenW) px = mouseX - panelW - 12;
            if (py + panelH > screenH) py = screenH - panelH - 4;
            if (py < 4) py = 4;
            if (px < 4) px = 4;

            context.fill(px, py, px + panelW, py + panelH, 0xF00a0a12);
            context.fill(px, py, px + panelW, py + 1, accent);
            context.fill(px, py + panelH - 1, px + panelW, py + panelH, accent);
            context.fill(px, py, px + 1, py + panelH, accent);
            context.fill(px + panelW - 1, py, px + panelW, py + panelH, accent);

            context.drawText(textRenderer, "§lSHULKER", px + padding, py + 3, accent, false);

            int gridX = px + padding;
            int gridY = py + titleH + padding;
            for (int i = 0; i < cols * rows; i++) {
                int col = i % cols, row = i / cols;
                int sx = gridX + col * slot, sy = gridY + row * slot;
                context.fill(sx, sy, sx + slot - 2, sy + slot - 2, 0x44000000);
                context.fill(sx, sy, sx + slot - 2, sy + 1, 0x33000000);
                context.fill(sx, sy, sx + 1, sy + slot - 2, 0x33000000);
            }
            for (int i = 0; i < Math.min(items.size(), cols * rows); i++) {
                int col = i % cols, row = i / cols;
                int sx = gridX + col * slot + 1, sy = gridY + row * slot + 1;
                ItemStack item = items.get(i);
                context.drawItem(item, sx, sy);
                context.drawStackOverlay(textRenderer, item, sx, sy);
            }
            context.drawText(textRenderer, "§7" + items.size() + " items", px + padding, py + panelH - 10, 0xFF8A8A9C, false);
        } catch (Throwable ignored) {}
    }
}