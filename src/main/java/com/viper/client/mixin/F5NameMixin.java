package com.viper.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class F5NameMixin {

    @Inject(method = "updateRenderState", at = @At("TAIL"), require = 0)
    private void viper_f5OwnName(Object entity, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options == null) return;
            if (mc.options.getPerspective().isFirstPerson()) return;
            if (entity != mc.player) return;
            if (state.nameLabelPos == null) {
                state.nameLabelPos = new Vec3d(0.0, mc.player.getHeight() + 0.5, 0.0);
            }
        } catch (Throwable ignored) {}
    }
}