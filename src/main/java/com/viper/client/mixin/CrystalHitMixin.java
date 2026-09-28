package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MinecraftClient.class)
public class CrystalHitMixin {

    @Inject(method = "doAttack", at = @At("HEAD"), require = 0)
    private void viper_crystalHitThrough(CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            // nur wenn wir auf einen block zeigen (obsidian/bed)
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (hit.getType() != HitResult.Type.BLOCK) return;

            BlockPos blockPos = bhr.getBlockPos();
            // suchen ob ein end-crystal über diesem block ist
            Box searchBox = new Box(
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    blockPos.getX() + 1.0, blockPos.getY() + 3.0, blockPos.getZ() + 1.0
            );
            List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class, searchBox, e -> e.isAlive());
            if (crystals.isEmpty()) return;

            // nächsten crystal attacken
            EndCrystalEntity target = crystals.get(0);
            // nur wenn in reichweite (4.5 blöcke)
            double dist = mc.player.getEyePos().distanceTo(target.getPos());
            if (dist > 5.0) return;

            // attack durchführen
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(mc.player.getActiveHand());
            // vanilla attack unterdrücken
            ci.cancel();
        } catch (Throwable ignored) {}
    }
}