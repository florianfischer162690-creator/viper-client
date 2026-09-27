package com.viper.client.mixin;

import com.viper.client.util.TotemTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemPopMixin {

    private static final byte TOTEM_STATUS = 35;

    @Inject(method = "onEntityStatus", at = @At("HEAD"), require = 0)
    private void viper_onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        try {
            if (packet.getStatus() != TOTEM_STATUS) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;
            Entity e = packet.getEntity(mc.world);
            if (e instanceof PlayerEntity p) {
                TotemTracker.registerPop(p.getUuid());
            }
        } catch (Throwable ignored) {}
    }
}