package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.EchestViewer;
import com.viper.client.util.ServerTabs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class EchestViewerMixin {

    @Inject(method = "init", at = @At("TAIL"), require = 0)
    private void viper_echestViewer(CallbackInfo ci) {
        try {
            if (!Config.donutEchestViewer) return;
            if (!ServerTabs.showDonutTab()) return;

            HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
            if (!(screen instanceof GenericContainerScreen)) return;

            ScreenHandler handler = screen.getScreenHandler();
            if (!(handler instanceof GenericContainerScreenHandler gcHandler)) return;

            Text title = screen.getTitle();
            if (title == null) return;
            String titleStr = title.getString().toLowerCase();
            if (!titleStr.contains("ender") && !titleStr.contains("echest") && !titleStr.contains("ec")) return;

            Inventory inv = gcHandler.getInventory();
            if (inv == null) return;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (stack != null && !stack.isEmpty()) {
                    if (sb.length() > 0) sb.append(" · ");
                    String name = stack.getName().getString();
                    if (stack.getCount() > 1) {
                        sb.append(stack.getCount()).append("x ").append(name);
                    } else {
                        sb.append(name);
                    }
                }
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            String owner = "self";
            if (mc.player != null) {
                owner = mc.player.getName().getString();
            }
            EchestViewer.setContents(owner, sb.toString());
        } catch (Throwable ignored) {}
    }
}