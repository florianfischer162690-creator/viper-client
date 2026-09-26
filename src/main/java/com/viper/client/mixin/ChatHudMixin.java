package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true)
    private Text viper_addTimestamp(Text original) {
        if (!Config.chatTimestamps) return original;
        try {
            String time = LocalTime.now().format(TIME_FORMAT);
            MutableText prefix = Text.literal("§8[" + time + "] §r");
            return prefix.append(original);
        } catch (Throwable ignored) {
            return original;
        }
    }
}