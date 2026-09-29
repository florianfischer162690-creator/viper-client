package com.viper.client.gui;

import com.viper.client.config.Config;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ChatTabsUI {

    public static final String[] TABS = {"ALL", "PUBLIC", "PARTY", "GUILD", "WHISPER"};
    public static final String[] TAB_IDS = {"all", "public", "party", "guild", "whisper"};

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            try {
                render(context);
            } catch (Throwable ignored) {}
        });
    }

    private static void render(DrawContext context) {
        if (!Config.chatTabs) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (!(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen)) return;

        int accent = Config.getAccent();

        int baseX = 3;
        int baseY = mc.getWindow().getScaledHeight() - 46;
        int tabH = 14;
        int gap = 2;

        int cx = baseX;
        for (int i = 0; i < TABS.length; i++) {
            String label = TABS[i];
            int tw = mc.textRenderer.getWidth(label) + 12;
            int tx = cx;
            int ty = baseY;

            boolean active = Config.activeChatTab != null && Config.activeChatTab.equals(TAB_IDS[i]);

            int bg = active ? accent : 0xCC1c1228;
            int border = active ? accent : 0xFF2a1c3d;

            context.fill(tx, ty, tx + tw, ty + tabH, bg);
            context.fill(tx, ty, tx + tw, ty + 1, border);
            context.fill(tx, ty + tabH - 1, tx + tw, ty + tabH, border);
            context.fill(tx, ty, tx + 1, ty + tabH, border);
            context.fill(tx + tw - 1, ty, tx + tw, ty + tabH, border);

            int textColor = active ? 0xFFFFFFFF : 0xFF8A8A9C;
            context.drawText(mc.textRenderer, label, tx + 6, ty + 3, textColor, false);

            cx += tw + gap;
        }
    }

    public static boolean handleClick(double mx, double my) {
        if (!Config.chatTabs) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;

        int baseX = 3;
        int baseY = mc.getWindow().getScaledHeight() - 46;
        int tabH = 14;
        int gap = 2;

        int cx = baseX;
        for (int i = 0; i < TABS.length; i++) {
            String label = TABS[i];
            int tw = mc.textRenderer.getWidth(label) + 12;
            if (mx >= cx && mx <= cx + tw && my >= baseY && my <= baseY + tabH) {
                Config.activeChatTab = TAB_IDS[i];
                Config.save();
                if (mc.player != null) {
                    mc.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fChat tab: §f" + label), true);
                }
                return true;
            }
            cx += tw + gap;
        }
        return false;
    }
}