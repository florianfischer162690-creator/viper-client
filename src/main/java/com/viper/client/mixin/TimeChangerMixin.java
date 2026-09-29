package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class TimeChangerMixin {

    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_timeOfDay(CallbackInfoReturnable<Long> cir) {
        try {
            if (!Config.timeChanger) return;
            if (Config.timeValue < 0) return;
            cir.setReturnValue((long) Config.timeValue);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_skyDarkness(net.minecraft.world.HeightLimitView heightLimitView, CallbackInfoReturnable<Double> cir) {
        try {
            if (!Config.timeChanger) return;
            if (Config.timeValue < 0) return;
            // tag → sky darkness weit unten (nichts ist dunkel)
            if (Config.timeValue >= 0 && Config.timeValue < 13000) {
                cir.setReturnValue(-1000.0);
            }
        } catch (Throwable ignored) {}
    }
}