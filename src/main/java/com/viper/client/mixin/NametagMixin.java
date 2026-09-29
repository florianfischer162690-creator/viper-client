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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class NametagMixin<T, S extends EntityRenderState> {

    @Inject(method = "updateRenderState", at = @At("TAIL"), require = 0)
    private void viper_addTitle(T entity, S state, float tickDelta, CallbackInfo ci) {
        try {
            if (!Config.showTitle) return;
            if (state == null || state.displayName == null) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            String myName = mc.player.getName().getString();
            if (!state.displayName.getString().contains(myName)) return;

            String titleId = Config.currentTitle;
            if (titleId == null || titleId.equals("none")) return;

            Titles.Title title = Titles.getById(titleId);
            if (title == null || title.name.isEmpty()) return;

            MutableText prefix = Text.literal("[" + title.name + "] ")
                    .withColor(title.color & 0x00FFFFFF);
            state.displayName = prefix.append(state.displayName);
        } catch (Throwable ignored) {}
    }
}