package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class CustomF3Mixin {

    @Inject(method = "getLeftText", at = @At("RETURN"), require = 0)
    private void viper_leftText(CallbackInfoReturnable<List<String>> cir) {
        try {
            if (!Config.customF3) return;
            List<String> lines = cir.getReturnValue();
            if (lines == null) return;

            lines.removeIf(line -> {
                if (line == null) return false;
                String s = line.toLowerCase();
                if (!Config.f3ShowChunk && (s.contains("chunk") || s.contains("section"))) return true;
                if (!Config.f3ShowBiome && s.contains("biome")) return true;
                if (!Config.f3ShowLight && s.contains("light")) return true;
                if (!Config.f3ShowEntities && s.contains("entities") && !s.contains("e:")) return true;
                if (!Config.f3ShowLooking && s.contains("looking at")) return true;
                if (!Config.f3ShowSounds && s.contains("sound")) return true;
                return false;
            });

            if (Config.f3ShowPing) {
                var mc = net.minecraft.client.MinecraftClient.getInstance();
                if (mc.getNetworkHandler() != null && mc.player != null) {
                    var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                    int ping = entry != null ? entry.getLatency() : 0;
                    lines.add("§a[Viper] §fPing: §a" + ping + "ms");
                }
            }

            if (Config.f3ShowMem) {
                Runtime rt = Runtime.getRuntime();
                long used = (rt.totalMemory() - rt.freeMemory()) / (1024L * 1024L);
                long max = rt.maxMemory() / (1024L * 1024L);
                lines.add("§a[Viper] §fRAM: §a" + used + "/" + max + "MB");
            }
        } catch (Throwable ignored) {}
    }

    @Inject(method = "getRightText", at = @At("RETURN"), require = 0)
    private void viper_rightText(CallbackInfoReturnable<List<String>> cir) {
        try {
            if (!Config.customF3) return;
            List<String> lines = cir.getReturnValue();
            if (lines == null) return;

            if (!Config.f3ShowSystemInfo) {
                lines.removeIf(line -> {
                    if (line == null) return false;
                    String s = line.toLowerCase();
                    return s.contains("java") || s.contains("os:") || s.contains("gpu:") || s.contains("opengl");
                });
            }

            lines.add("§a[Viper V1] §7F3 customized");
        } catch (Throwable ignored) {}
    }
}