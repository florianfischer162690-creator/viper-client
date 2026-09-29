package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.hud.element.DeathInfoElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class DeathInfoMixin {

    @Inject(method = "onDeath", at = @At("HEAD"), require = 0)
    private void viper_onDeath(DamageSource source, CallbackInfo ci) {
        try {
            if (!Config.showDeathInfo) return;
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            if (player == null) return;

            String dim = "unknown";
            try {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.world != null) {
                    dim = mc.world.getRegistryKey().getValue().toString();
                }
            } catch (Throwable ignored) {}

            String cause = "";
            try {
                if (source != null && source.getName() != null) {
                    cause = source.getName();
                }
            } catch (Throwable ignored) {}

            DeathInfoElement.registerDeath(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    dim,
                    cause
            );
        } catch (Throwable ignored) {}
    }
}