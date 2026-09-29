package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class CrystalPlaceMixin {

    @Shadow private int itemUseCooldown;
    @Shadow private int attackCooldown;

    // block-abbau unterdrücken wenn crystal in der hand + obsidian im visier
    private static boolean suppressNextAttack = false;

    @Inject(method = "doItemUse", at = @At("HEAD"), require = 0)
    private void viper_crystalPlace(CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            if (!(mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL))) return;

            // cooldowns reset für instant-place
            this.itemUseCooldown = 0;
            this.attackCooldown = 0;

            // nächsten attack unterdrücken (damit kein block-abbau passiert)
            HitResult hit = mc.crosshairTarget;
            if (hit instanceof BlockHitResult && hit.getType() == HitResult.Type.BLOCK) {
                suppressNextAttack = true;
            }
        } catch (Throwable ignored) {}
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_suppressAttack(CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            if (!suppressNextAttack) return;
            suppressNextAttack = false;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            // nur unterdrücken wenn crystal in der hand
            if (mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)) {
                ci.cancel();
            }
        } catch (Throwable ignored) {}
    }
}