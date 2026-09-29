package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public class FullbrightShadowsMixin {

    @ModifyVariable(
            method = "update",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private float viper_fullbrightShadows(float original) {
        try {
            if (Config.fullbright) return 1.0f;
        } catch (Throwable ignored) {}
        return original;
    }
}