package com.viper.client;

import com.viper.client.config.Config;
import com.viper.client.gui.NotepadScreen;
import com.viper.client.gui.ViperStartMenu;
import com.viper.client.hud.HudRenderer;
import com.viper.client.hud.element.CrosshairElement;
import com.viper.client.hud.element.StopwatchElement;
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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

    public static Object gammaOptionRef = null;
    public static String pendingChatCommand = null;

    private static KeyBinding toggleHudKey;
    private static KeyBinding openMenuKey;
    private static KeyBinding toggleSprintKey;
    private static KeyBinding toggleFullbrightKey;
    private static KeyBinding noFogKey;
    private static KeyBinding openNotepadKey;
    private static KeyBinding freecamKey;
    private static KeyBinding coordSnapperKey;
    private static KeyBinding stopwatchKey;
    private static KeyBinding freelookKey;

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
                if (Config.freecamEnabled) {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player != null) {
                        Config.freecamYaw = mc.player.getYaw();
                        Config.freecamPitch = mc.player.getPitch();
                    }
                }
            } catch (Throwable ignored) {}

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
        freecamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, KeyBinding.Category.MISC));
        coordSnapperKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.coord_snapper", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, KeyBinding.Category.MISC));
        stopwatchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.stopwatch", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, KeyBinding.Category.MISC));
        freelookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viper.freelook", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, KeyBinding.Category.MISC));

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

            while (freecamKey.wasPressed()) {
                Config.freecamEnabled = !Config.freecamEnabled;
                if (Config.freecamEnabled && client.player != null) {
                    Config.freecamX = client.player.getX();
                    Config.freecamY = client.player.getEyeY();
                    Config.freecamZ = client.player.getZ();
                    Config.freecamYaw = client.player.getYaw();
                    Config.freecamPitch = client.player.getPitch();
                }
                if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fFreecam " + (Config.freecamEnabled ? "§aon" : "§coff")), true);
                Config.save();
            }

            while (coordSnapperKey.wasPressed()) {
                try {
                    if (!Config.donutCoordSnapper) continue;
                    if (client.player == null) continue;
                    int x = (int) client.player.getX();
                    int y = (int) client.player.getY();
                    int z = (int) client.player.getZ();
                    String coords = "/coords " + x + " " + y + " " + z;
                    if (client.keyboard != null) {
                        client.keyboard.setClipboard(coords);
                    }
                    client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fCoords copied: §f" + x + " " + y + " " + z), false);
                } catch (Throwable ignored) {}
            }

            while (stopwatchKey.wasPressed()) {
                StopwatchElement.toggle();
                if (client.player != null) {
                    client.player.sendMessage(net.minecraft.text.Text.literal(
                            "§a[Viper] §fStopwatch " + (StopwatchElement.isRunning() ? "§astarted" : "§cpaused")), true);
                }
            }

            // Freelook — halten → aktiv, loslassen → zurück
            try {
                boolean freelookHeld = freelookKey.isPressed();
                if (freelookHeld && client.player != null && !Config.freelookActive) {
                    Config.freelookActive = true;
                    Config.freelookYaw = client.player.getYaw();
                    Config.freelookPitch = client.player.getPitch();
                } else if (!freelookHeld && Config.freelookActive) {
                    Config.freelookActive = false;
                }
            } catch (Throwable ignored) {}

            try {
                if (client.player != null) {
                    String pending = pendingChatCommand;
                    if (pending != null && !pending.isEmpty()) {
                        pendingChatCommand = null;
                        handleWaypointCommand(client, pending);
                    }
                }
            } catch (Throwable ignored) {}

            try {
                long window = client.getWindow().getHandle();
                if (client.currentScreen == null) {
                    for (Map.Entry<String, Integer> entry : new HashMap<>(Config.keybinds).entrySet()) {
                        String id = entry.getKey();
                        if ("zoom".equals(id)) continue;
                        if (id.startsWith("macro")) continue;
                        if ("stopwatch".equals(id)) continue;
                        if ("freelook".equals(id)) continue;
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

            // FREECAM movement
            try {
                if (Config.freecamEnabled && client.player != null) {
                    client.options.forwardKey.setPressed(false);
                    client.options.backKey.setPressed(false);
                    client.options.leftKey.setPressed(false);
                    client.options.rightKey.setPressed(false);
                    client.options.jumpKey.setPressed(false);

                    client.player.setVelocity(0, client.player.getVelocity().y, 0);
                    client.player.setSprinting(false);

                    long window = client.getWindow().getHandle();
                    double speed = 0.5;
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) speed = 0.15;

                    double yawRad = Math.toRadians(Config.freecamYaw);
                    double pitchRad = Math.toRadians(Config.freecamPitch);

                    double forwardX = -Math.sin(yawRad);
                    double forwardZ = Math.cos(yawRad);
                    double forwardY = -Math.sin(pitchRad);

                    double rightX = Math.cos(yawRad);
                    double rightZ = Math.sin(yawRad);

                    double dx = 0, dy = 0, dz = 0;
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) { dx += forwardX * speed; dy += forwardY * speed; dz += forwardZ * speed; }
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) { dx -= forwardX * speed; dy -= forwardY * speed; dz -= forwardZ * speed; }
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) { dx -= rightX * speed; dz -= rightZ * speed; }
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) { dx += rightX * speed; dz += rightZ * speed; }
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) dy += speed;
                    if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) dy -= speed;

                    Config.freecamX += dx;
                    Config.freecamY += dy;
                    Config.freecamZ += dz;
                }
            } catch (Throwable ignored) {}

            if (client.player != null && client.currentScreen == null) {
                if (Config.toggleSprint) client.options.sprintKey.setPressed(true);
            }

            try {
                if (client.player != null) {
                    StatusEffectInstance existing = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
                    if (Config.fullbright) {
                        if (existing == null || existing.getDuration() < 200) {
                            client.player.addStatusEffect(new StatusEffectInstance(
                                    StatusEffects.NIGHT_VISION,
                                    999999,
                                    0,
                                    false,
                                    false,
                                    false
                            ));
                        }
                    } else {
                        if (existing != null) {
                            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                        }
                    }
                }
            } catch (Throwable ignored) {}
        });

        LOGGER.info("[Viper V1] ready.");
    }

    public static void handleWaypointCommand(MinecraftClient client, String command) {
        try {
            String trimmed = command.trim();
            if (!trimmed.startsWith("/wp")) return;

            String[] parts = trimmed.split("\\s+");
            if (client.player == null || client.world == null) return;

            if (parts.length == 1 || (parts.length >= 2 && parts[1].equalsIgnoreCase("list"))) {
                client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fWaypoints (" + Config.waypoints.size() + "/10):"), false);
                if (Config.waypoints.isEmpty()) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§7keine waypoints gesetzt"), false);
                } else {
                    for (Config.WaypointData wp : Config.waypoints.values()) {
                        client.player.sendMessage(net.minecraft.text.Text.literal(
                                "§7- §f" + wp.name + " §7(" + wp.x + ", " + wp.y + ", " + wp.z + ")"), false);
                    }
                }
                return;
            }

            String sub = parts[1].toLowerCase();

            if (sub.equals("set")) {
                if (parts.length < 3) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§c[Viper] §fUsage: /wp set <name>"), false);
                    return;
                }
                String name = parts[2];
                int px = (int) client.player.getX();
                int py = (int) client.player.getY();
                int pz = (int) client.player.getZ();
                String dim = client.world.getRegistryKey().getValue().toString();
                if (com.viper.client.util.Waypoints.add(name, px, py, pz, dim)) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fWaypoint '§f" + name + "§f' gesetzt §7(" + px + ", " + py + ", " + pz + ")"), false);
                } else {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§c[Viper] §fMax 10 waypoints erreicht"), false);
                }
            } else if (sub.equals("del") || sub.equals("delete") || sub.equals("remove")) {
                if (parts.length < 3) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§c[Viper] §fUsage: /wp del <name>"), false);
                    return;
                }
                if (com.viper.client.util.Waypoints.remove(parts[2])) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §fWaypoint '§f" + parts[2] + "§f' gelöscht"), false);
                } else {
                    client.player.sendMessage(net.minecraft.text.Text.literal("§c[Viper] §fWaypoint nicht gefunden"), false);
                }
            } else {
                client.player.sendMessage(net.minecraft.text.Text.literal("§7[Viper] /wp set <name> | /wp del <name> | /wp list"), false);
            }
        } catch (Throwable ignored) {}
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
            case "fullbright": Config.fullbright = !Config.fullbright; break;
            case "nofog": Config.noFog = !Config.noFog; break;
            case "fpsboost": Config.fpsBoost = !Config.fpsBoost; break;
            case "stopwatch": Config.showStopwatch = !Config.showStopwatch; break;
            case "clock": Config.showClock = !Config.showClock; break;
            case "daycounter": Config.showDayCounter = !Config.showDayCounter; break;
            case "playtime": Config.showPlaytime = !Config.showPlaytime; break;
            case "memory": Config.showMemory = !Config.showMemory; break;
            case "serveraddress": Config.showServerAddress = !Config.showServerAddress; break;
            case "pinggraph": Config.showPingGraph = !Config.showPingGraph; break;
            case "cooldown": Config.showCooldown = !Config.showCooldown; break;
            case "tntcountdown": Config.showTntCountdown = !Config.showTntCountdown; break;
            case "blockoutline": Config.thickBlockOutline = !Config.thickBlockOutline; break;
            case "weatherchanger": Config.weatherChanger = !Config.weatherChanger; break;
            case "timechanger": Config.timeChanger = !Config.timeChanger; break;
            case "fogcustomizer": Config.fogCustomizer = !Config.fogCustomizer; break;
            case "chattabs": Config.chatTabs = !Config.chatTabs; break;
            case "chatheads": Config.chatHeads = !Config.chatHeads; break;
            case "customf3": Config.customF3 = !Config.customF3; break;
        }
        Config.save();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal("§a[Viper] §f" + id), true);
    }
}