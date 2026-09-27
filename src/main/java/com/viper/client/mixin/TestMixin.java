package com.viper.client.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class TestMixin {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), require = 1)
    private void viper_test(CallbackInfo ci) {
    }
}