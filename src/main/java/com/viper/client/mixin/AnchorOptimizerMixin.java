package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class AnchorOptimizerMixin {

    private static final byte DEATH_ANIMATION_STATUS = 3;

    @Inject(method = "onEntityStatus", at = @At("HEAD"), require = 0)
    private void viper_anchorOpt(EntityStatusS2CPacket packet, CallbackInfo ci) {
        try {
            if (!Config.anchorOptimizer) return;
            if (packet.getStatus() != DEATH_ANIMATION_STATUS) return;

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;

            Entity entity = packet.getEntity(mc.world);
            if (entity == null) return;

            // respawn-anchor explosion → entity wird sofort entfernt
            // (status 3 für non-crystal entities im radius der explosion)
            double dx = entity.getX() - mc.player.getX();
            double dz = entity.getZ() - mc.player.getZ();
            double dist2 = dx * dx + dz * dz;
            // nur entities in der nähe vom spieler (damit kein distant-kill passiert)
            if (dist2 > 25.0) return;

            // entity sofort entfernen wenn's ein tnt/other anchor-related entity ist
            // (tnt + falling blocks + item entities)
            String typeName = entity.getType().toString();
            if (typeName.contains("tnt") || typeName.contains("item") || typeName.contains("falling")) {
                mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);
            }
        } catch (Throwable ignored) {}
    }
}