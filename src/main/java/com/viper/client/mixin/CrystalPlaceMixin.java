package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class CrystalPlaceMixin {

    @Shadow private int itemUseCooldown;
    @Shadow private int attackCooldown;

    private static int placeTicker = 0;
    private static final int MIN_TICKS_BETWEEN_PLACES = 1;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_crystalPlace(CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            if (!(mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL))) return;

            if (placeTicker > 0) {
                ci.cancel();
                return;
            }
            placeTicker = MIN_TICKS_BETWEEN_PLACES;

            this.itemUseCooldown = 0;
            this.attackCooldown = 0;
        } catch (Throwable ignored) {}
    }

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void viper_crystalTick(CallbackInfo ci) {
        try {
            if (placeTicker > 0) placeTicker--;
        } catch (Throwable ignored) {}
    }
}