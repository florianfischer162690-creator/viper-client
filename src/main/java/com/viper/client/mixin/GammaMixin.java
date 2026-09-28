package com.viper.client.mixin;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public class GammaMixin {

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true, require = 0)
    private void viper_fullbrightGamma(CallbackInfoReturnable<Object> cir) {
        try {
            if (!Config.fullbright) return;
            if (ViperClient.gammaOptionRef == null) return;
            if ((Object) this != ViperClient.gammaOptionRef) return;
            cir.setReturnValue(20.0);
        } catch (Throwable ignored) {}
    }
}