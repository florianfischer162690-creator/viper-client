package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemPhysicsMixin {

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void viper_itemPhysics(ItemEntityRenderState state, MatrixStack matrices,
                                    net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                                    int light, CallbackInfo ci) {
        try {
            if (!Config.itemPhysics) return;
            // item liegt flach am boden — rotation nur um X-achse
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        } catch (Throwable ignored) {}
    }
}