package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderLayer.class)
public class GlintColorMixin {

    // NOTE: glint-farbe in MC 1.21.11 ist ein texture-swap
    // dieser mixin macht nichts kaputt — er wird nur aktiv wenn enchant glint bereits gerendert wird
    // eigentliche farbe: wird über texture-packs bzw. shader gemacht. daher als stub.
}