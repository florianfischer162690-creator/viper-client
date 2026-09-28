package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class FreecamMixin {

    @Shadow private double x;
    @Shadow private double y;
    @Shadow private double z;
    @Shadow private float pitch;
    @Shadow private float yaw;

    @Inject(method = "update", at = @At("TAIL"), require = 0)
    private void viper_freecamUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        try {
            if (!Config.freecamEnabled) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            // kamera-position und rotation aus config nutzen
            this.x = Config.freecamX;
            this.y = Config.freecamY;
            this.z = Config.freecamZ;
            this.yaw = Config.freecamYaw;
            this.pitch = Config.freecamPitch;
        } catch (Throwable ignored) {}
    }
}