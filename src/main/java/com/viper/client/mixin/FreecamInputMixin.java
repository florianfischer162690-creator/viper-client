package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.input.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class FreecamInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"), require = 0)
    private void viper_freecamBlockInput(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        try {
            if (!Config.freecamEnabled) return;
            this.playerInput = new PlayerInput(false, false, false, false, false, false, false);
        } catch (Throwable ignored) {}
    }
}