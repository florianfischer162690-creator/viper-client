package com.viper.client.hud.element;

import com.viper.client.config.Config;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class BlockOutlineRenderer {

    public static void register() {
        WorldRenderEvents.LAST.register(BlockOutlineRenderer::onRender);
    }

    private static void onRender(WorldRenderContext ctx) {
        try {
            if (!Config.thickBlockOutline) return;
            if (Config.blockOutlineExpansion <= 0) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (hit.getType() != HitResult.Type.BLOCK) return;

            BlockPos pos = bhr.getBlockPos();
            var state = mc.world.getBlockState(pos);
            if (state == null) return;

            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape == null || shape.isEmpty()) return;

            double expand = Config.blockOutlineExpansion / 100.0;
            double cx = ctx.camera().getPos().x;
            double cy = ctx.camera().getPos().y;
            double cz = ctx.camera().getPos().z;

            double px = pos.getX() - cx;
            double py = pos.getY() - cy;
            double pz = pos.getZ() - cz;

            MatrixStack matrices = ctx.matrixStack();
            if (matrices == null) return;

            VertexConsumer vc = ctx.consumers();
            if (vc == null) return;

            matrices.push();
            shape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
                try {
                    Box b = new Box(
                            px + x1 - expand, py + y1 - expand, pz + z1 - expand,
                            px + x2 + expand, py + y2 + expand, pz + z2 + expand
                    );
                    VertexRendering.drawBox(matrices, vc, b, 0.0f, 0.0f, 0.0f, 1.0f);
                } catch (Throwable ignored) {}
            });
            matrices.pop();
        } catch (Throwable ignored) {}
    }
}