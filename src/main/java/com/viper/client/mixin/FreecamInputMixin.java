package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class FreecamInputMixin extends Input {

    @Shadow public abstract void tick(boolean slowDown, float slowDownFactor);

    @Inject(method = "tick", at = @At("TAIL"), require = 0)
    private void viper_freecamBlockInput(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        try {
            if (!Config.freecamEnabled) return;
            this.playerInput.forward(false);
            this.playerInput.backward(false);
            this.playerInput.left(false);
            this.playerInput.right(false);
            this.playerInput.jump(false);
            this.playerInput.sneak(false);
        } catch (Throwable ignored) {}
    }
}