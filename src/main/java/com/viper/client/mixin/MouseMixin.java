package com.viper.client.mixin;

import com.viper.client.util.ClickTracker;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void viper_onMouseButton(long window, Object button, int action, CallbackInfo ci) {
        // action: 1 = press, 0 = release
        if (action != 1) return;
        if (button == null) return;

        try {
            String name = button.toString().toUpperCase();
            // button-objekt ist ein enum in MC 1.21.11
            if (name.contains("LEFT")) {
                ClickTracker.registerLeft();
            } else if (name.contains("RIGHT")) {
                ClickTracker.registerRight();
            } else if (name.contains("MIDDLE")) {
                // mittelklick ignorieren
            }
        } catch (Throwable ignored) {
            // fail-safe — crash verhindern
        }
    }
}