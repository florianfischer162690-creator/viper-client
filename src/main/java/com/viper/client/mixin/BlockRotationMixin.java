package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public class BlockRotationMixin {

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void viper_blockRotHead(
            BlockRenderView world,
            List<BlockState> states,
            BlockState state,
            BlockPos pos,
            MatrixStack matrices,
            net.minecraft.client.render.VertexConsumer vertexConsumer,
            boolean cull, int seed,
            CallbackInfo ci) {
        try {
            if (!Config.antiBlockRotation) return;
            if (state == null || pos == null) return;
            if (!isRotationBlock(state)) return;

            long posSeed = pos.asLong();
            Random rng = Random.create(posSeed);
            int rot = rng.nextInt(4);

            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot * 90.0f));
            matrices.translate(-0.5, -0.5, -0.5);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "render", at = @At("RETURN"), require = 0)
    private void viper_blockRotReturn(
            BlockRenderView world,
            List<BlockState> states,
            BlockState state,
            BlockPos pos,
            MatrixStack matrices,
            net.minecraft.client.render.VertexConsumer vertexConsumer,
            boolean cull, int seed,
            CallbackInfo ci) {
        try {
            if (!Config.antiBlockRotation) return;
            if (state == null || pos == null) return;
            if (!isRotationBlock(state)) return;

            matrices.pop();
        } catch (Throwable ignored) {}
    }

    private boolean isRotationBlock(BlockState state) {
        return state.isOf(Blocks.DEEPSLATE)
                || state.isOf(Blocks.BEDROCK)
                || state.isOf(Blocks.COBBLED_DEEPSLATE)
                || state.isOf(Blocks.TUFF)
                || state.isOf(Blocks.ANDESITE)
                || state.isOf(Blocks.DIORITE)
                || state.isOf(Blocks.GRANITE)
                || state.isOf(Blocks.GRAVEL);
    }
}