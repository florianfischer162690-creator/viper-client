package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class WeatherChangerMixin {

    @Shadow private void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering) {}

    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_weatherRain(float delta, CallbackInfoReturnable<Float> cir) {
        try {
            if (!Config.weatherChanger) return;
            if ("clear".equals(Config.weatherType)) cir.setReturnValue(0.0f);
            else if ("rain".equals(Config.weatherType)) cir.setReturnValue(1.0f);
            else if ("thunder".equals(Config.weatherType)) cir.setReturnValue(1.0f);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_weatherThunder(float delta, CallbackInfoReturnable<Float> cir) {
        try {
            if (!Config.weatherChanger) return;
            if ("clear".equals(Config.weatherType)) cir.setReturnValue(0.0f);
            else if ("rain".equals(Config.weatherType)) cir.setReturnValue(0.0f);
            else if ("thunder".equals(Config.weatherType)) cir.setReturnValue(1.0f);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_isRaining(CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Config.weatherChanger) return;
            if ("clear".equals(Config.weatherType)) cir.setReturnValue(false);
            else if ("rain".equals(Config.weatherType) || "thunder".equals(Config.weatherType)) cir.setReturnValue(true);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_isThundering(CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Config.weatherChanger) return;
            if ("thunder".equals(Config.weatherType)) cir.setReturnValue(true);
            else if (Config.weatherChanger) cir.setReturnValue(false);
        } catch (Throwable ignored) {}
    }
}