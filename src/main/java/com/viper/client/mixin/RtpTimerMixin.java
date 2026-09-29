package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.RtpTracker;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class RtpTimerMixin {

    // matcht z.b. "Bitte warte 30 Sekunden" oder "Cooldown: 5 Minuten" oder "60s"
    private static final Pattern SECONDS_PATTERN = Pattern.compile("(\\d+)\\s*(?:sek|second|sec|s\\b)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MINUTES_PATTERN = Pattern.compile("(\\d+)\\s*(?:min|minute|m\\b)", Pattern.CASE_INSENSITIVE);

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), require = 0)
    private void viper_rtpListener(Text message, CallbackInfo ci) {
        try {
            if (!Config.showRtpTimer) return;
            if (!ServerTabs.showHugoTab()) return;
            if (message == null) return;

            String text = message.getString();
            if (text == null) return;

            String lower = text.toLowerCase();

            // nur bei rtp / cooldown nachrichten
            boolean relevant = lower.contains("rtp") || lower.contains("random teleport")
                    || lower.contains("cooldown") || lower.contains("warte") || lower.contains("wait");
            if (!relevant) return;

            long cooldownMs = -1L;

            Matcher mMin = MINUTES_PATTERN.matcher(text);
            if (mMin.find()) {
                try {
                    long min = Long.parseLong(mMin.group(1));
                    cooldownMs = min * 60_000L;
                } catch (Throwable ignored) {}
            }

            if (cooldownMs < 0) {
                Matcher mSec = SECONDS_PATTERN.matcher(text);
                if (mSec.find()) {
                    try {
                        long sec = Long.parseLong(mSec.group(1));
                        cooldownMs = sec * 1000L;
                    } catch (Throwable ignored) {}
                }
            }

            if (cooldownMs > 0) {
                RtpTracker.registerRtp(cooldownMs);
            }
        } catch (Throwable ignored) {}
    }
}