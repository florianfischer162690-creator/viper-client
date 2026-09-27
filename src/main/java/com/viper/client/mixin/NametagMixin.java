package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.Titles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public abstract class NametagMixin<S extends EntityRenderState> {

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private Text viper_addTitleToNametag(Text original) {
        try {
            if (!Config.showTitle) return original;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return original;

            String myName = mc.player.getName().getString();
            if (!original.getString().contains(myName)) return original;

            String titleId = Config.currentTitle;
            if (titleId == null || titleId.equals("none")) return original;

            Titles.Title title = Titles.getById(titleId);
            if (title == null || title.name.isEmpty()) return original;

            MutableText prefix = Text.literal("[" + title.name + "] ")
                    .withColor(title.color & 0x00FFFFFF);
            return prefix.append(original);
        } catch (Throwable ignored) {
            return original;
        }
    }
}