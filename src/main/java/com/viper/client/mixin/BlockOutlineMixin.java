package com.viper.client.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {

    @Inject(method = "drawBlockOutline", at = @At("TAIL"), require = 0)
    private void viper_thickBlockOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            double cameraX, double cameraY, double cameraZ,
            Object shapeContext,
            int color,
            float tickDelta,
            CallbackInfo ci) {
        try {
            // stub — wird aktiviert wenn wir die richtige signatur kennen
            // der mixin applied jetzt sauber, tut aber noch nichts
        } catch (Throwable ignored) {}
    }
}