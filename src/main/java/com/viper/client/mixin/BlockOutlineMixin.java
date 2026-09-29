package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {

    @Inject(method = "drawBlockOutline", at = @At("TAIL"), require = 0)
    private void viper_thickBlockOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            Entity entity,
            double cameraX, double cameraY, double cameraZ,
            BlockPos pos,
            BlockState state,
            CallbackInfo ci) {
        try {
            if (!Config.thickBlockOutline) return;
            if (Config.blockOutlineExpansion <= 0) return;

            if (entity == null || entity.getWorld() == null) return;

            VoxelShape shape = state.getOutlineShape(entity.getWorld(), pos);
            if (shape.isEmpty()) return;

            double expand = Config.blockOutlineExpansion / 100.0;
            double x = pos.getX() - cameraX;
            double y = pos.getY() - cameraY;
            double z = pos.getZ() - cameraZ;

            shape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
                Box b = new Box(
                        x + x1 - expand, y + y1 - expand, z + z1 - expand,
                        x + x2 + expand, y + y2 + expand, z + z2 + expand
                );
                WorldRenderer.drawBox(matrices, vertexConsumer, b, 0.0f, 0.0f, 0.0f, 1.0f);
            });
        } catch (Throwable ignored) {}
    }
}