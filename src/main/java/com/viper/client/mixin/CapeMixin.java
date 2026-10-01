package com.viper.client.mixin;

import com.viper.client.cosmetics.CosmeticsManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class CapeMixin {

    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true, require = 0)
    private void viper_capeTexture(CallbackInfoReturnable<Identifier> cir) {
        try {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
            if (player == null) return;

            String username = player.getName().getString();
            String capeId = CosmeticsManager.getActiveCape(username);
            if (capeId == null) return;

            Identifier tex = CosmeticsManager.getCapeTexture(capeId);
            if (tex != null) {
                cir.setReturnValue(tex);
            }
        } catch (Throwable ignored) {}
    }
}