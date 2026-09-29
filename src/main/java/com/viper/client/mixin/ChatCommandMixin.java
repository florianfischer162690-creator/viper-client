package com.viper.client.mixin;

import com.viper.client.ViperClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatCommandMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_catchWaypointCommand(String chatText, boolean addToHistory, CallbackInfo ci) {
        try {
            if (chatText == null) return;
            String trimmed = chatText.trim();
            if (!trimmed.startsWith("/wp")) return;

            ViperClient.pendingChatCommand = trimmed;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen instanceof ChatScreen cs) {
                cs.close();
            }
            ci.cancel();
        } catch (Throwable ignored) {}
    }
}