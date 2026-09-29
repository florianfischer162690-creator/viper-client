package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.Titles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerTitleMixin {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), require = 0)
    private void viper_playerTitle(PlayerEntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
        try {
            if (!Config.showTitle) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            String myName = mc.player.getName().getString();
            if (!text.getString().contains(myName)) return;

            String titleId = Config.currentTitle;
            if (titleId == null || titleId.equals("none")) return;

            Titles.Title title = Titles.getById(titleId);
            if (title == null || title.name.isEmpty()) return;

            // prefix über eigenen namen — der render wird von vanilla mit dem original-text gemacht
            // hier können wir nichts direkt ändern. deshalb: zusätzliche render-call NACH dem vanilla-label.
        } catch (Throwable ignored) {}
    }
}