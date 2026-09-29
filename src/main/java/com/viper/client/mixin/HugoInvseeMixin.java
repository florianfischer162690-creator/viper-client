package com.viper.client.mixin;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class HugoInvseeMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), require = 0)
    private void viper_hugoInvsee(Text message, CallbackInfo ci) {
        try {
            if (!Config.hugoAutoInvsee) return;
            if (!ServerTabs.showHugoTab()) return;
            if (message == null) return;

            String text = message.getString();
            if (!text.contains("Gegner gefunden:")) return;

            int idx = text.indexOf("Gegner gefunden:");
            if (idx < 0) return;
            String after = text.substring(idx + "Gegner gefunden:".length()).trim();
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < after.length(); i++) {
                char c = after.charAt(i);
                if (c == '.' || c == ',' || c == ' ' || c == '!') break;
                name.append(c);
            }
            String opponent = name.toString().trim();
            if (opponent.isEmpty()) return;

            final String opponentName = opponent;
            new Thread(() -> {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null && mc.player.networkHandler != null) {
                    mc.execute(() -> {
                        try {
                            mc.player.networkHandler.sendChatCommand("invsee " + opponentName);
                            ViperClient.LOGGER.info("[HugoInvsee] sent /invsee " + opponentName);
                        } catch (Throwable ignored) {}
                    });
                }
            }).start();
        } catch (Throwable ignored) {}
    }
}