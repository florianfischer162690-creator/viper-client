package com.viper.client;

import com.viper.client.config.Config;
import com.viper.client.gui.NotepadScreen;
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
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ViperClient implements ClientModInitializer {
    public static final String MOD_ID = "viper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // cache der gamma-option damit mixin nicht rekursiv getGamma() ruft
    public static Object gammaOptionRef = null;

    private static KeyBinding toggleHudKey;
    private static KeyBinding openMenuKey;
    private static KeyBinding toggleSprintKey;
    private static KeyBinding toggleFullbrightKey;
    private static KeyBinding noFogKey;
    private static KeyBinding openNotepadKey;

    private static boolean lastLeftDown = false;
    private static boolean lastRightDown = false;
    private static final Map<String, Boolean> keyPressedState = new HashMap<>();
    private static boolean zoomed = false;

    private static final boolean[] macroPressedState = new boolean[6];
    private static final long[] macroLastSend = new long[6];

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Viper V1] initializing...");

        Config.load();

        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.options != null) {
                gammaOptionRef = mc.options.getGamma();
            }
        } catch (Throwable ignored) {}

        HudRenderer.init();

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
                    CrosshairElement.triggerHitMarker();
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

            try {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (Config.lowHealthWarning && mc.player != null && mc.player.getHealth() < 6.0f && mc.player.getHealth() > 0) {
                    int w = mc.getWindow().getScaledWidth();
                    int h = mc.getWindow().getScaledHeight();
                    int alpha = (int) (60 + Math.sin(System.currentTimeMillis() / 200.0) * 30);
                    int color = (alpha << 24) | 0x00FF0000;
                    int thickness = 4;
                    context.fill(0, 0, w, thickness, color);
                    context.fill(0, h - thickness, w, h, color);
                    context.fill(0, 0, thickness, h, color);
                    context.fill(w - thickness, 0, w, h, color);
                }
            } catch (Throwable ignored) {}

            HudRenderer.render(context, 0.0f);
            CrosshairElement.render(context);
        });

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, KeyBinding.Category.MISC));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.Category.MISC));
        toggleSprintKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.toggle_sprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyBinding.Category.MISC));
        toggleFullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, KeyBinding.Category.MISC));
        noFogKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.no_fog", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, KeyBinding.Category.MISC));
        openNotepadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.open_notepad", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F7, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                Config.hudEnabled = !Config.hudEnabled;
                Config.save();
                if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fHUD " + (Config.hudEnabled ? "§aon" : "§coff")), true);
            }

            while (openMenuKey.wasPressed()) {
                if (client.currentScreen instanceof ViperStartMenu) client.setScreen(null);
                else client.setScreen(new ViperStartMenu());
            }

            while (toggleSprintKey.wasPressed()) {
                Config.toggleSprint = !Config.toggleSprint;
                Config.save();
                if (!Config.toggleSprint) client.options.sprintKey.setPressed(false);
                if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fSprint " + (Config.toggleSprint ? "§aon" : "§coff")), true);
            }

            while (toggleFullbrightKey.wasPressed()) {
                Config.fullbright = !Config.fullbright;
                Config.save();
                if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fFullbright " + (Config.fullbright ? "§aon" : "§coff")), true);
            }

            while (noFogKey.wasPressed()) {
                Config.noFog = !Config.noFog;
                Config.save();
                if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fNoFog " + (Config.noFog ? "§aon" : "§coff")), true);
            }

            while (openNotepadKey.wasPressed()) {
                if (client.currentScreen instanceof NotepadScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new NotepadScreen());
                }
            }

            try {
                long window = client.getWindow().getHandle();
                if (client.currentScreen == null) {
                    for (Map.Entry<String, Integer> entry : new HashMap<>(Config.keybinds).entrySet()) {
                        String id = entry.getKey();
                        if ("zoom".equals(id)) continue;
                        if (id.startsWith("macro")) continue;
                        int key = entry.getValue();
                        if (key <= 0) continue;
                        boolean isDown = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
                        boolean wasDown = keyPressedState.getOrDefault(id, false);
                        if (isDown && !wasDown) {
                            if ("notepad".equals(id)) {
                                client.setScreen(new NotepadScreen());
                            } else {
                                toggleModule(id);
                            }
                        }
                        keyPressedState.put(id, isDown);
                    }

                    for (int i = 0; i < 6; i++) {
                        int key = Config.getKeybind("macro" + (i + 1));
                        if (key <= 0) { macroPressedState[i] = false; continue; }
                        boolean isDown = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
                        if (isDown && !macroPressedState[i]) {
                            long now = System.currentTimeMillis();
                            if (now - macroLastSend[i] > 250) {
                                sendMacro(client, i);
                                macroLastSend[i] = now;
                            }
                        }
                        macroPressedState[i] = isDown;
                    }
                }
            } catch (Throwable ignored) {}

            try {
                if (Config.zoomEnabled) {
                    int zoomKeycode = Config.getKeybind("zoom");
                    boolean zoomPressed = false;
                    if (zoomKeycode > 0) {
                        long window = client.getWindow().getHandle();
                        zoomPressed = GLFW.glfwGetKey(window, zoomKeycode) == GLFW.GLFW_PRESS;
                    }
                    if (zoomPressed && !zoomed) {
                        Config.defaultFov = client.options.getFov().getValue();
                        client.options.getFov().setValue(Config.zoomFov);
                        zoomed = true;
                    } else if (!zoomPressed && zoomed) {
                        client.options.getFov().setValue(Config.defaultFov);
                        zoomed = false;
                    }
                } else if (zoomed) {
                    client.options.getFov().setValue(Config.defaultFov);
                    zoomed = false;
                }
            } catch (Throwable ignored) {}

            if (client.player != null && client.currentScreen == null) {
                if (Config.toggleSprint) client.options.sprintKey.setPressed(true);
            }
        });

        LOGGER.info("[Viper V1] ready.");
    }

    private static void sendMacro(MinecraftClient client, int index) {
        try {
            if (client.player == null) return;
            if (index < 0 || index >= 6) return;
            if (!Config.macroEnabled[index]) return;
            String text = Config.macroTexts[index];
            if (text == null || text.isEmpty()) return;
            if (client.player.networkHandler == null) return;
            if (text.startsWith("/")) {
                client.player.networkHandler.sendChatCommand(text.substring(1));
            } else {
                client.player.networkHandler.sendChatMessage(text);
            }
        } catch (Throwable ignored) {}
    }

    private static void toggleModule(String id) {
        switch (id) {
            case "watermark": Config.showWatermark = !Config.showWatermark; break;
            case "fps": Config.showFps = !Config.showFps; break;
            case "cps": Config.showCps = !Config.showCps; break;
            case "coords": Config.showCoords = !Config.showCoords; break;
            case "ping": Config.showPing = !Config.showPing; break;
            case "armor": Config.showArmor = !Config.showArmor; break;
            case "potion": Config.showPotion = !Config.showPotion; break;
            case "keystrokes": Config.showKeystrokes = !Config.showKeystrokes; break;
            case "reach": Config.showReach = !Config.showReach; break;
            case "combo": Config.showCombo = !Config.showCombo; break;
            case "direction": Config.showDirection = !Config.showDirection; break;
            case "speed": Config.showSpeed = !Config.showSpeed; break;
            case "hudtoggle": Config.hudEnabled = !Config.hudEnabled; break;
            case "lowhealth": Config.lowHealthWarning = !Config.lowHealthWarning; break;
            case "inventoryhud": Config.showInventoryHud = !Config.showInventoryHud; break;
            case "togglesprint": Config.toggleSprint = !Config.toggleSprint; break;
            case "togglesneak": Config.toggleSneak = !Config.toggleSneak; break;
            case "customcrosshair": Config.showCustomCrosshair = !Config.showCustomCrosshair; break;
            case "targetindicator": Config.showTargetIndicator = !Config.showTargetIndicator; break;
            case "healthindicator": Config.showHealthIndicator = !Config.showHealthIndicator; break;
            case "hitmarker": Config.showHitMarker = !Config.showHitMarker; break;
            case "appleskin": Config.showAppleskin = !Config.showAppleskin; break;
            case "shulkerpreview": Config.showShulkerPreview = !Config.showShulkerPreview; break;
            case "zoom": Config.zoomEnabled = !Config.zoomEnabled; break;
            case "fullbright":
                Config.fullbright = !Config.fullbright;
                break;
            case "nofog": Config.noFog = !Config.noFog; break;
            case "fpsboost": Config.fpsBoost = !Config.fpsBoost; break;
        }
        Config.save();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §f" + id), true);
    }
}