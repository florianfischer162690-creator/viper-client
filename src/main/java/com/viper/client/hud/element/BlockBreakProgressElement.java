package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class BlockBreakProgressElement extends HudElement {

    private static final int COLOR_BG    = 0x66000000;
    private static final int COLOR_VALUE = 0xFFE6E6E6;
    private static final int PADDING = 3;

    private static BlockPos lastPos = null;
    private static long lastChangeTime = 0;
    private static float lastProgress = 0f;

    @Override
    public String getId() { return "blockbreakprogress"; }

    @Override
    public boolean isEnabled() { return Config.showBlockBreakProgress; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 220; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null || mc.world == null) return 0;
        int accent = Config.getAccent();

        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult bhr) || hit.getType() != HitResult.Type.BLOCK) return 0;

        BlockPos pos = bhr.getBlockPos();
        // progress ermitteln via break-progress des spielers
        int stage = mc.interactionManager != null ? mc.interactionManager.getBlockBreakingProgress() : -1;
        if (stage < 0) return 0;

        String label = "MINE";
        String value = ((stage + 1) * 10) + "%";

        int labelW = mc.textRenderer.getWidth(label);
        int valueW = mc.textRenderer.getWidth(value);
        int width = labelW + 6 + valueW + PADDING * 2;
        int height = mc.textRenderer.fontHeight + PADDING * 2 + 6;

        context.fill(x, y, x + width, y + height, COLOR_BG);
        context.fill(x, y, x + 2, y + height, accent);

        context.drawText(mc.textRenderer, label, x + PADDING + 3, y + PADDING, accent, true);
        context.drawText(mc.textRenderer, value, x + PADDING + 3 + labelW + 6, y + PADDING, COLOR_VALUE, true);

        // kleiner progress-balken unten
        int barY = y + height - 4;
        int barW = width - 6;
        context.fill(x + 3, barY, x + 3 + barW, barY + 2, 0x44000000);
        int fillW = (int) (barW * ((stage + 1) / 10.0f));
        if (fillW > 0) context.fill(x + 3, barY, x + 3 + fillW, barY + 2, accent);

        return height;
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return mc.textRenderer.getWidth("MINE") + 6 + mc.textRenderer.getWidth("100%") + PADDING * 2;
    }
}