package com.viper.client.hud;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import com.viper.client.hud.element.AbsorptionElement;
import com.viper.client.hud.element.AdminHudElement;
import com.viper.client.hud.element.ArmorElement;
import com.viper.client.hud.element.AttackIndicatorElement;
import com.viper.client.hud.element.BalanceTrackerElement;
import com.viper.client.hud.element.BlockBreakProgressElement;
import com.viper.client.hud.element.ClockElement;
import com.viper.client.hud.element.CombatTimerElement;
import com.viper.client.hud.element.ComboElement;
import com.viper.client.hud.element.CooldownElement;
import com.viper.client.hud.element.CoordsElement;
import com.viper.client.hud.element.CpsElement;
import com.viper.client.hud.element.DayCounterElement;
import com.viper.client.hud.element.DeathInfoElement;
import com.viper.client.hud.element.DirectionElement;
import com.viper.client.hud.element.EchestViewerElement;
import com.viper.client.hud.element.FpsElement;
import com.viper.client.hud.element.InventoryHudElement;
import com.viper.client.hud.element.KeystrokesElement;
import com.viper.client.hud.element.MemoryElement;
import com.viper.client.hud.element.PingElement;
import com.viper.client.hud.element.PingGraphElement;
import com.viper.client.hud.element.PlaytimeElement;
import com.viper.client.hud.element.PotionElement;
import com.viper.client.hud.element.ReachElement;
import com.viper.client.hud.element.RegionMapElement;
import com.viper.client.hud.element.RtpTimerElement;
import com.viper.client.hud.element.SaturationElement;
import com.viper.client.hud.element.ServerAddressElement;
import com.viper.client.hud.element.ShieldStatusElement;
import com.viper.client.hud.element.SpawnerNotifierElement;
import com.viper.client.hud.element.SpeedElement;
import com.viper.client.hud.element.StopwatchElement;
import com.viper.client.hud.element.TntCountdownElement;
import com.viper.client.hud.element.TotemWarningElement;
import com.viper.client.hud.element.WatermarkElement;
import com.viper.client.hud.element.WaypointsElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    public static final List<HudElement> ELEMENTS = new ArrayList<>();
    public static KeystrokesElement keystrokesElement;
    public static ArmorElement armorElement;
    public static InventoryHudElement inventoryElement;

    public static void init() {
        ELEMENTS.clear();
        ELEMENTS.add(new WatermarkElement());
        ELEMENTS.add(new FpsElement());
        ELEMENTS.add(new CpsElement());
        ELEMENTS.add(new CoordsElement());
        ELEMENTS.add(new PingElement());
        ELEMENTS.add(new PotionElement());
        ELEMENTS.add(new ReachElement());
        ELEMENTS.add(new ComboElement());
        ELEMENTS.add(new DirectionElement());
        ELEMENTS.add(new SpeedElement());
        ELEMENTS.add(new BlockBreakProgressElement());
        ELEMENTS.add(new SaturationElement());
        ELEMENTS.add(new AbsorptionElement());
        ELEMENTS.add(new ShieldStatusElement());
        ELEMENTS.add(new WaypointsElement());
        ELEMENTS.add(new TotemWarningElement());
        ELEMENTS.add(new AdminHudElement());
        ELEMENTS.add(new StopwatchElement());
        ELEMENTS.add(new ClockElement());
        ELEMENTS.add(new DayCounterElement());
        ELEMENTS.add(new PlaytimeElement());
        ELEMENTS.add(new MemoryElement());
        ELEMENTS.add(new ServerAddressElement());
        ELEMENTS.add(new PingGraphElement());
        ELEMENTS.add(new CooldownElement());
        ELEMENTS.add(new TntCountdownElement());
        ELEMENTS.add(new AttackIndicatorElement());
        ELEMENTS.add(new DeathInfoElement());
        ELEMENTS.add(new CombatTimerElement());
        ELEMENTS.add(new SpawnerNotifierElement());
        ELEMENTS.add(new RegionMapElement());
        ELEMENTS.add(new BalanceTrackerElement());
        ELEMENTS.add(new EchestViewerElement());
        ELEMENTS.add(new RtpTimerElement());
        keystrokesElement = new KeystrokesElement();
        armorElement = new ArmorElement();
        inventoryElement = new InventoryHudElement();
        ViperClient.LOGGER.info("[Viper] HUD initialized");
    }

    public static void render(DrawContext context, float tickDelta) {
        if (!Config.hudEnabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        int scaledW = mc.getWindow().getScaledWidth();
        int scaledH = mc.getWindow().getScaledHeight();

        for (HudElement el : ELEMENTS) {
            if (!el.isEnabled()) continue;
            Config.ElementPos pos = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());

            int elW = (int) (el.getWidth(mc) * pos.scale);
            int elH = (int) (el.getHeight(mc) * pos.scale);

            if (pos.x < 0) pos.x = 0;
            if (pos.y < 0) pos.y = 0;
            if (pos.x + elW > scaledW) pos.x = Math.max(0, scaledW - elW);
            if (pos.y + elH > scaledH) pos.y = Math.max(0, scaledH - elH);

            context.getMatrices().push();
            context.getMatrices().translate((float) pos.x, (float) pos.y, 0.0f);
            context.getMatrices().scale(pos.scale, pos.scale, 1.0f);
            el.render(context, mc, 0, 0);
            context.getMatrices().pop();
        }

        if (keystrokesElement.isEnabled()) {
            Config.ElementPos pos = Config.getPos(
                    keystrokesElement.getId(),
                    keystrokesElement.getDefaultX(mc),
                    keystrokesElement.getDefaultY(mc)
            );

            int elW = (int) (keystrokesElement.getWidth(mc) * pos.scale);
            int elH = (int) (keystrokesElement.getHeight(mc) * pos.scale);

            if (pos.x < 0) pos.x = 0;
            if (pos.y < 0) pos.y = 0;
            if (pos.x + elW > scaledW) pos.x = Math.max(0, scaledW - elW);
            if (pos.y + elH > scaledH) pos.y = Math.max(0, scaledH - elH);

            context.getMatrices().push();
            context.getMatrices().translate((float) pos.x, (float) pos.y, 0.0f);
            context.getMatrices().scale(pos.scale, pos.scale, 1.0f);
            keystrokesElement.render(context, mc, 0, 0);
            context.getMatrices().pop();
        }

        if (Config.showInventoryHud && inventoryElement.isEnabled()) {
            Config.ElementPos pos = Config.getPos(
                    inventoryElement.getId(),
                    inventoryElement.getDefaultX(),
                    inventoryElement.getDefaultY()
            );

            int elW = (int) (inventoryElement.getWidth(mc) * pos.scale);
            int elH = (int) (inventoryElement.getHeight(mc) * pos.scale);

            if (pos.x < 0) pos.x = 0;
            if (pos.y < 0) pos.y = 0;
            if (pos.x + elW > scaledW) pos.x = Math.max(0, scaledW - elW);
            if (pos.y + elH > scaledH) pos.y = Math.max(0, scaledH - elH);

            context.getMatrices().push();
            context.getMatrices().translate((float) pos.x, (float) pos.y, 0.0f);
            context.getMatrices().scale(pos.scale, pos.scale, 1.0f);
            inventoryElement.render(context, mc, 0, 0);
            context.getMatrices().pop();
        }

        if (Config.showArmor && armorElement.isEnabled()) {
            int armorW = armorElement.getWidth(mc);
            int armorH = armorElement.getHeight(mc);
            int ax = (scaledW / 2) - 91 - 29 - armorW - 6;
            int ay = (scaledH - 11) - (armorH / 2);
            context.getMatrices().push();
            context.getMatrices().translate((float) ax, (float) ay, 0.0f);
            armorElement.render(context, mc, 0, 0);
            context.getMatrices().pop();
        }
    }

    public abstract static class HudElement {
        public abstract String getId();
        public abstract boolean isEnabled();
        public abstract int render(DrawContext context, MinecraftClient mc, int x, int y);
        public int getDefaultX() { return 4; }
        public int getDefaultY() { return 4; }
        public int getWidth(MinecraftClient mc) { return 80; }
        public int getHeight(MinecraftClient mc) { return mc.textRenderer.fontHeight + 6; }
    }
}