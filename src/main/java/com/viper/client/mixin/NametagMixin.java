package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.Titles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class NametagMixin<S extends EntityRenderState> {

    @Inject(
            method = "renderLabelIfPresent",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void viper_titleNametag(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
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

            // vanilla rendern abbrechen
            ci.cancel();

            // eigenen text rendern (titel + name) an der gleichen position
            MutableText prefix = Text.literal("[" + title.name + "] ")
                    .withColor(title.color & 0x00FFFFFF);
            Text finalText = prefix.append(text);

            // um den vanilla-render zu reproduzieren — hier nur der text-anteil
            // matrices ist schon richtig positioniert
            // wir nutzen die vanilla-mechanik von MC's text-renderer
            try {
                var matricesCopy = new MatrixStack();
                matricesCopy.peek().getPositionMatrix().set(matrices.peek().getPositionMatrix());
                matricesCopy.scale(-0.025f, -0.025f, 0.025f);
                matricesCopy.translate(0, -10.0, 0);

                float bgOpacity = mc.options.getTextBackgroundOpacity().getValue().floatValue();
                int bgColor = (int)(bgOpacity * 255.0f) << 24;

                mc.textRenderer.draw(finalText, -mc.textRenderer.getWidth(finalText) / 2.0f, 0, 0x20FFFFFF, false, matricesCopy.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, bgColor, light);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
    }
}