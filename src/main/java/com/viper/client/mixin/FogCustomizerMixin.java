package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class FogCustomizerMixin {

    @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true, require = 0)
    private static void viper_applyFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            Vector4f fogColor,
            float viewDistance,
            boolean thickFog,
            float tickDelta,
            CallbackInfoReturnable<Fog> cir) {
        try {
            if (!Config.fogCustomizer) return;
            if (Config.fogStart >= Config.fogEnd) return;

            Fog fog = new Fog(Config.fogStart, Config.fogEnd, FogShape.SPHERE,
                    fogColor.x, fogColor.y, fogColor.z, fogColor.w);
            cir.setReturnValue(fog);
        } catch (Throwable ignored) {}
    }
}