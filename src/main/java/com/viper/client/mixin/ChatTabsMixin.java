package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.ChatTabManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatTabsMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_chatTabFilter(Text message, CallbackInfo ci) {
        try {
            if (!Config.chatTabs) return;
            if (message == null) return;

            String raw = message.getString();
            if (raw == null) return;

            String tab = Config.activeChatTab;
            if (tab == null || tab.equals("all")) return;

            boolean matches = false;
            switch (tab) {
                case "party":
                    matches = raw.contains("[Party]") || raw.toLowerCase().contains("party >");
                    break;
                case "guild":
                    matches = raw.contains("[Guild]") || raw.contains("[Clan]")
                            || raw.toLowerCase().contains("guild >")
                            || raw.toLowerCase().contains("clan >");
                    break;
                case "whisper":
                    matches = raw.contains("whispers") || raw.contains("[MSG]")
                            || raw.contains("→ you") || raw.contains("-> you");
                    break;
                case "public":
                    matches = !raw.contains("[Party]") && !raw.contains("[Guild]")
                            && !raw.contains("[Clan]") && !raw.contains("whispers")
                            && !raw.contains("→ you");
                    break;
            }

            if (!matches) {
                ChatTabManager.archive(raw);
                ci.cancel();
            }
        } catch (Throwable ignored) {}
    }
}