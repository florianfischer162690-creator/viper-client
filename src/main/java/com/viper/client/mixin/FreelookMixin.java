package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class FreelookMixin {

    @Shadow private void setRotation(float yaw, float pitch) {}

    @Inject(method = "update", at = @At("TAIL"), require = 0)
    private void viper_freelook(
            World area, Entity focusedEntity, boolean thirdPerson,
            boolean inverseView, float tickDelta, CallbackInfo ci) {
        try {
            if (!Config.freelookActive) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            this.setRotation(Config.freelookYaw, Config.freelookPitch);
        } catch (Throwable ignored) {}
    }
}