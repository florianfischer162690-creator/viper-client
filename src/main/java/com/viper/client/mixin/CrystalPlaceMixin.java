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

    @Inject(method = "doItemUse", at = @At("HEAD"), require = 0)
    private void viper_crystalPlace(CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            // wenn crystal in der main-hand → cooldown reset für instant-place
            if (mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)) {
                this.itemUseCooldown = 0;
            }
        } catch (Throwable ignored) {}
    }
}