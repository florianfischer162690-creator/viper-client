package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.ChestFilterUtil;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class ChestFilterMixin {

    @Inject(method = "renderSlot", at = @At("TAIL"), require = 0)
    private void viper_chestFilterSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        try {
            if (!Config.donutChestFilter) return;
            if (!ServerTabs.showDonutTab()) return;
            if (slot == null) return;

            ItemStack stack = slot.getStack();
            if (!ChestFilterUtil.isValuable(stack)) return;

            // slot-koordinaten (im GUI-koordinatenraum)
            int x = slot.x;
            int y = slot.y;
            int size = 16;

            int accent = 0xFFFFD700;
            int alpha = 150 + (int) (Math.sin(System.currentTimeMillis() / 300.0) * 50);

            // glühender rand um den slot
            int borderCol = (accent & 0x00FFFFFF) | ((alpha & 0xFF) << 24);

            context.fill(x - 1, y - 1, x + size + 1, y, borderCol);
            context.fill(x - 1, y + size, x + size + 1, y + size + 1, borderCol);
            context.fill(x - 1, y, x, y + size, borderCol);
            context.fill(x + size, y, x + size + 1, y + size, borderCol);
        } catch (Throwable ignored) {}
    }
}