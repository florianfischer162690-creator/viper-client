package com.viper.client;

import com.viper.client.config.Config;
import com.viper.client.gui.ViperStartMenu;
import com.viper.client.hud.HudRenderer;
import com.viper.client.hud.element.CrosshairElement;
import com.viper.client.util.AttackTracker;
import com.viper.client.util.ClickTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViperClient implements ClientModInitializer {
    public static final String MOD_ID = "viper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding toggleHudKey;
    private static KeyBinding openMenuKey;
    private static KeyBinding toggleSprintKey;

    private static boolean lastLeftDown = false;
    private static boolean lastRightDown = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Viper V1] initializing...");

        Config.load();
        HudRenderer.init();

        // attack-callback für reach + combo
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            try {
                if (player == MinecraftClient.getInstance().player && entity != null) {
                    Vec3d playerPos = player.getEyePos();
                    double ex = entity.getX();
                    double ey = entity.getY() + entity.getHeight() / 2.0;
                    double ez = entity.getZ();
                    double dx = playerPos.x - ex;
                    double dy = playerPos.y - ey;
                    double dz = playerPos.z - ez;
                    double reach = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    AttackTracker.registerHit(reach);
                }
            } catch (Throwable ignored) {}
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            try {
                long window = MinecraftClient.getInstance().getWindow().getHandle();
                boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

                if (leftDown && !lastLeftDown) ClickTracker.registerLeft();
                if (rightDown && !lastRightDown) ClickTracker.registerRight();

                lastLeftDown = leftDown;
                lastRightDown = rightDown;
            } catch (Throwable ignored) {}

            HudRenderer.render(context, 0.0f);
            CrosshairElement.render(context);
        });

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                KeyBinding.Category.MISC
        ));

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        toggleSprintKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.toggle_sprint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                Config.hudEnabled = !Config.hudEnabled;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("§a[Viper] §fHUD " + (Config.hudEnabled ? "§aon" : "§coff")),
                            true
                    );
                }
            }

            while (openMenuKey.wasPressed()) {
                if (client.currentScreen instanceof ViperStartMenu) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ViperStartMenu());
                }
            }

            while (toggleSprintKey.wasPressed()) {
                Config.toggleSprint = !Config.toggleSprint;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("§a[Viper] §fToggle Sprint " + (Config.toggleSprint ? "§aon" : "§coff")),
                            true
                    );
                }
                if (!Config.toggleSprint) {
                    client.options.sprintKey.setPressed(false);
                }
            }

            if (client.player != null && client.currentScreen == null) {
                if (Config.toggleSprint) {
                    client.options.sprintKey.setPressed(true);
                }
            }
        });

        LOGGER.info("[Viper V1] ready.");
    }
}