package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHeadsMixin {

    // match <spielername> am anfang der nachricht — typisch: "<Name> text"
    private static final Pattern PLAYER_PATTERN = Pattern.compile("^(?:§.)*<([A-Za-z0-9_]{2,16})>");

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true, require = 0)
    private Text viper_chatHead(Text original) {
        try {
            if (!Config.chatHeads) return original;
            if (original == null) return original;

            String raw = original.getString();
            if (raw == null || raw.isEmpty()) return original;

            Matcher m = PLAYER_PATTERN.matcher(raw);
            if (!m.find()) return original;

            String playerName = m.group(1);
            if (playerName == null || playerName.isEmpty()) return original;

            // head als text-symbol vor dem namen: z.b. "§7[§f<name>§7]§r <name> ..."
            // eigentliches head-rendering (2D gesicht) müsste via mixin in ChatHud.render passieren
            // hier: placeholder der den namen im format markiert
            MutableText prefix = Text.literal("§7[§a" + playerName.substring(0, 1).toUpperCase() + "§7]§r ");
            return prefix.append(original);
        } catch (Throwable ignored) {
            return original;
        }
    }
}