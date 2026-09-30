package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MinecraftClient.class)
public class CrystalHitMixin {

    @Shadow private int attackCooldown;

    @Inject(method = "doAttack", at = @At("HEAD"), require = 0)
    private void viper_resetAttackCooldown(CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            if (mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)) {
                this.attackCooldown = 0;
            }
        } catch (Throwable ignored) {}
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_crystalHitThrough(CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (hit.getType() != HitResult.Type.BLOCK) return;

            BlockPos blockPos = bhr.getBlockPos();
            Box searchBox = new Box(
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    blockPos.getX() + 1.0, blockPos.getY() + 3.0, blockPos.getZ() + 1.0
            );
            List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class, searchBox, e -> e.isAlive());
            if (crystals.isEmpty()) return;

            EndCrystalEntity target = crystals.get(0);
            double dist = mc.player.getEyePos().distanceTo(target.getPos());
            if (dist > 5.0) return;

            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(mc.player.getActiveHand());

            mc.world.removeEntity(target.getId(), Entity.RemovalReason.KILLED);
            this.attackCooldown = 0;

            cir.setReturnValue(true);
        } catch (Throwable ignored) {}
    }
}