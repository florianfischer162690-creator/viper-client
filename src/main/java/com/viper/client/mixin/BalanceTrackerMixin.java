package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.BalanceTracker;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class BalanceTrackerMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), require = 0)
    private void viper_balanceListener(Text message, CallbackInfo ci) {
        try {
            if (!Config.donutBalanceTracker) return;
            if (!ServerTabs.showDonutTab()) return;
            if (message == null) return;

            String text = message.getString();
            if (text == null || text.isEmpty()) return;

            // typische formate:
            // "Player hat 12345$"
            // "Balance von Player: 12345"
            // "Player's balance: 12345"
            // "$12345"
            String[] markers = {
                    " hat ", " balance: ", " Balance: ",
                    " Kontostand: ", " geld: ", " Geld: "
            };

            String foundPlayer = null;
            long foundBalance = -1;

            for (String marker : markers) {
                int idx = text.indexOf(marker);
                if (idx < 0) continue;

                // spielername vor dem marker
                String before = text.substring(0, idx).trim();
                String[] parts = before.split("\\s+");
                String candidate = parts[parts.length - 1].replaceAll("[^A-Za-z0-9_]", "");
                if (!candidate.isEmpty() && candidate.length() <= 16) {
                    foundPlayer = candidate;
                }

                // zahl nach dem marker
                String after = text.substring(idx + marker.length()).trim();
                StringBuilder num = new StringBuilder();
                for (int i = 0; i < after.length(); i++) {
                    char c = after.charAt(i);
                    if (Character.isDigit(c) || c == ',' || c == '.') num.append(c);
                    else if (num.length() > 0) break;
                }
                if (num.length() > 0) {
                    try {
                        String clean = num.toString().replace(",", "").replace(".", "");
                        foundBalance = Long.parseLong(clean);
                    } catch (Throwable ignored) {}
                }
                break;
            }

            if (foundPlayer != null && foundBalance >= 0) {
                BalanceTracker.setBalance(foundPlayer, foundBalance);
            }
        } catch (Throwable ignored) {}
    }
}