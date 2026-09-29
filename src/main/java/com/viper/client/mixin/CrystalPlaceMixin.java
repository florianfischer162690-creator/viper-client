package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
            if (mc.player == null || mc.world == null) return;
            if (mc.player.networkHandler == null) return;
            if (!(mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL))) return;

            if (placeTicker > 0) {
                ci.cancel();
                return;
            }

            // crosshair muss auf block zeigen
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (hit.getType() != HitResult.Type.BLOCK) return;

            // direktes packet senden ohne vanilla item-use cooldown
            Hand hand = mc.player.getMainHandStack().isOf(Items.END_CRYSTAL) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            BlockPos pos = bhr.getBlockPos();
            Direction side = bhr.getSide();

            // packet: block-place
            PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(
                    hand,
                    new BlockHitResult(bhr.getPos(), side, pos, false),
                    0
            );
            mc.player.networkHandler.sendPacket(packet);
            mc.player.swingHand(hand);

            placeTicker = MIN_TICKS_BETWEEN_PLACES;

            this.itemUseCooldown = 0;
            this.attackCooldown = 0;

            ci.cancel();
        } catch (Throwable ignored) {}
    }

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void viper_crystalTick(CallbackInfo ci) {
        try {
            if (placeTicker > 0) placeTicker--;
        } catch (Throwable ignored) {}
    }
}