package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class ScoreboardMixin {

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_scoreboardHide(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        try {
            if (Config.hideScoreboard) {
                ci.cancel();
                return;
            }
            if (Config.fakeScoreboard) {
                ci.cancel();
                renderFakeScoreboard(context, objective);
            }
        } catch (Throwable ignored) {}
    }

    private void renderFakeScoreboard(DrawContext context, ScoreboardObjective objective) {
        try {
            String raw = Config.scoreboardFakeText;
            if (raw == null || raw.isEmpty()) return;
            String[] lines = raw.split("\\|");
            if (lines.length == 0) return;

            net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
            if (mc == null) return;

            int screenW = mc.getWindow().getScaledWidth();
            int screenH = mc.getWindow().getScaledHeight();
            int lineH = mc.textRenderer.fontHeight + 1;

            int maxW = 0;
            for (String l : lines) {
                int w = mc.textRenderer.getWidth(l);
                if (w > maxW) maxW = w;
            }
            int padding = 3;
            int boxW = maxW + padding * 2;
            int boxH = lines.length * lineH + padding * 2;

            int x = screenW - boxW - 3;
            int y = (screenH - boxH) / 2;

            int accent = Config.getAccent();

            context.fill(x, y, x + boxW, y + boxH, 0x55000000);
            context.fill(x, y, x + boxW, y + 1, accent);
            context.fill(x, y + boxH - 1, x + boxW, y + boxH, accent);

            int ly = y + padding;
            for (String line : lines) {
                context.drawText(mc.textRenderer, line.trim(), x + padding, ly, 0xFFFFFFFF, false);
                ly += lineH;
            }
        } catch (Throwable ignored) {}
    }
}