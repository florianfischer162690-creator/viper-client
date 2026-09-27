package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.font.TextRenderer;
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

    @Shadow protected Slot focusedSlot;
    @Shadow protected TextRenderer textRenderer;

    @Inject(method = "render", at = @At("TAIL"))
    private void viper_shulkerOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            if (!Config.showShulkerPreview) return;
            if (focusedSlot == null) return;

            ItemStack stack = focusedSlot.getStack();
            if (stack == null || stack.isEmpty()) return;

            ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
            if (container == null) return;

            List<ItemStack> items = new ArrayList<>();
            for (ItemStack s : container.iterateNonEmpty()) items.add(s);
            if (items.isEmpty()) return;

            int accent = Config.getAccent();

            int cols = 9;
            int rows = 3;
            int slot = 18;
            int padding = 6;
            int titleH = 12;

            int panelW = cols * slot + padding * 2;
            int panelH = rows * slot + padding * 2 + titleH;

            int px = mouseX + 12;
            int py = mouseY - 12;

            int screenW = ((net.minecraft.client.gui.screen.Screen) (Object) this).width;
            int screenH = ((net.minecraft.client.gui.screen.Screen) (Object) this).height;

            if (px + panelW > screenW) px = mouseX - panelW - 12;
            if (py + panelH > screenH) py = screenH - panelH - 4;
            if (py < 4) py = 4;
            if (px < 4) px = 4;

            // panel bg
            context.fill(px, py, px + panelW, py + panelH, 0xF00a0a12);

            // border
            context.fill(px, py, px + panelW, py + 1, accent);
            context.fill(px, py + panelH - 1, px + panelW, py + panelH, accent);
            context.fill(px, py, px + 1, py + panelH, accent);
            context.fill(px + panelW - 1, py, px + panelW, py + panelH, accent);

            // title
            context.drawText(textRenderer, "§lSHULKER", px + padding, py + 3, accent, false);

            // slots-hintergrund
            int gridX = px + padding;
            int gridY = py + titleH + padding;

            for (int i = 0; i < cols * rows; i++) {
                int col = i % cols;
                int row = i / cols;
                int sx = gridX + col * slot;
                int sy = gridY + row * slot;
                context.fill(sx, sy, sx + slot - 2, sy + slot - 2, 0x44000000);
                context.fill(sx, sy, sx + slot - 2, sy + 1, 0x33000000);
                context.fill(sx, sy, sx + 1, sy + slot - 2, 0x33000000);
            }

            // items rendern
            for (int i = 0; i < Math.min(items.size(), cols * rows); i++) {
                int col = i % cols;
                int row = i / cols;
                int sx = gridX + col * slot + 1;
                int sy = gridY + row * slot + 1;
                ItemStack item = items.get(i);
                context.drawItem(item, sx, sy);
                context.drawStackOverlay(textRenderer, item, sx, sy);
            }

            // unterer info-text
            context.drawText(textRenderer, "§7" + items.size() + " items", px + padding, py + panelH - 10, 0xFF8A8A9C, false);

        } catch (Throwable ignored) {}
    }
}