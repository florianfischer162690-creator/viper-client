package com.viper.client.mixin;

import com.viper.client.util.ServerTabs;
import com.viper.client.util.StaffTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class TabListMixin {

    @Inject(method = "onPlayerList", at = @At("TAIL"), require = 0)
    private void viper_onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        try {
            if (!ServerTabs.showDonutTab()) return;

            ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) (Object) this;
            Collection<PlayerListEntry> entries = handler.getPlayerList();
            List<String> names = new ArrayList<>();
            for (PlayerListEntry entry : entries) {
                if (entry.getDisplayName() != null) {
                    names.add(entry.getDisplayName().getString());
                }
            }
            StaffTracker.update(names);
        } catch (Throwable ignored) {}
    }
}