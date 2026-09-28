package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class CrystalOptimizerMixin {

    private static final byte DEATH_ANIMATION_STATUS = 3;

    @Inject(method = "onEntityStatus", at = @At("HEAD"), require = 0)
    private void viper_crystalOpt(EntityStatusS2CPacket packet, CallbackInfo ci) {
        try {
            if (!Config.crystalOptimizer) return;
            if (packet.getStatus() != DEATH_ANIMATION_STATUS) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;

            Entity entity = packet.getEntity(mc.world);
            if (!(entity instanceof EndCrystalEntity crystal)) return;

            // crystal sofort aus client-world entfernen → kein render-delay
            mc.world.removeEntity(crystal.getId(), Entity.RemovalReason.KILLED);
        } catch (Throwable ignored) {}
    }
}