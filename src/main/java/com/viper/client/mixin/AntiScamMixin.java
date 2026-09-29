package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.AntiScamFilter;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class AntiScamMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_antiScam(Text message, CallbackInfo ci) {
        try {
            if (!Config.donutAntiScam) return;
            if (!ServerTabs.showDonutTab()) return;
            if (message == null) return;

            String text = message.getString();
            if (AntiScamFilter.isScam(text)) {
                ci.cancel();
            }
        } catch (Throwable ignored) {}
    }
}