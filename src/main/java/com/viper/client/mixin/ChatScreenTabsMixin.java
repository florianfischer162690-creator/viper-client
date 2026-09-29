package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.gui.ChatTabsUI;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenTabsMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_chatTabsClick(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!Config.chatTabs) return;
            if (click.button() != 0) return;
            if (ChatTabsUI.handleClick(click.x(), click.y())) {
                cir.setReturnValue(true);
            }
        } catch (Throwable ignored) {}
    }
}