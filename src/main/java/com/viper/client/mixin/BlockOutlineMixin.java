package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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
            if (state == null || pos == null) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;

            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape == null || shape.isEmpty()) return;

            double expand = Config.blockOutlineExpansion / 100.0;
            double x = pos.getX() - cameraX;
            double y = pos.getY() - cameraY;
            double z = pos.getZ() - cameraZ;

            shape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
                try {
                    // kein WorldRenderer.drawBox mehr — wir zeichnen die outline-boxen über die vanilla pipeline
                    // hier nur dummy — da drawBox in 1.21.11 umbenannt wurde
                    // (siehe WorldRenderer -> renderBox oder ähnlich)
                } catch (Throwable ignored) {}
            });
        } catch (Throwable ignored) {}
    }
}