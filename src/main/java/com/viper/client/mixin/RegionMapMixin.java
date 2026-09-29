package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.RegionTracker;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class RegionMapMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), require = 0)
    private void viper_regionListener(Text message, CallbackInfo ci) {
        try {
            if (!Config.donutRegionMap) return;
            if (!ServerTabs.showDonutTab()) return;
            if (message == null) return;

            String text = message.getString();
            if (text == null || text.isEmpty()) return;

            // typische region-nachrichten:
            // "Willkommen in Region: X"
            // "Du betrittst Region X"
            // "Region: X"
            String[] prefixes = {
                    "Willkommen in Region:",
                    "Willkommen in Region ",
                    "Du betrittst Region ",
                    "Region:",
                    "Region "
            };

            for (String prefix : prefixes) {
                int idx = text.indexOf(prefix);
                if (idx >= 0) {
                    String after = text.substring(idx + prefix.length()).trim();
                    StringBuilder name = new StringBuilder();
                    for (int i = 0; i < after.length(); i++) {
                        char c = after.charAt(i);
                        if (c == '!' || c == '.' || c == ',' || c == '§' || (c == ' ' && name.length() > 0)) break;
                        name.append(c);
                    }
                    String region = name.toString().trim();
                    if (!region.isEmpty()) {
                        RegionTracker.setRegion(region);
                    }
                    return;
                }
            }
        } catch (Throwable ignored) {}
    }
}