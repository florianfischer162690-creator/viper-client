package com.viper.client.hud;

import com.viper.client.ViperClient;
import com.viper.client.config.Config;
import com.viper.client.hud.element.ArmorElement;
import com.viper.client.hud.element.ComboElement;
import com.viper.client.hud.element.CoordsElement;
import com.viper.client.hud.element.CpsElement;
import com.viper.client.hud.element.DirectionElement;
import com.viper.client.hud.element.FpsElement;
import com.viper.client.hud.element.KeystrokesElement;
import com.viper.client.hud.element.PingElement;
import com.viper.client.hud.element.PotionElement;
import com.viper.client.hud.element.ReachElement;
import com.viper.client.hud.element.SpeedElement;
import com.viper.client.hud.element.TotemPopElement;
import com.viper.client.hud.element.WatermarkElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    public static final List<HudElement> ELEMENTS = new ArrayList<>();
    public static KeystrokesElement keystrokesElement;
    public static ArmorElement armorElement;

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
        ELEMENTS.add(new TotemPopElement());
        keystrokesElement = new KeystrokesElement();
        armorElement = new ArmorElement();
        ViperClient.LOGGER.info("[Viper] HUD initialized");
    }

    public static void render(DrawContext context, float tickDelta) {
        if (!Config.hudEnabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        for (HudElement el : ELEMENTS) {
            if (!el.isEnabled()) continue;
            Config.ElementPos pos = Config.getPos(el.getId(), el.getDefaultX(), el.getDefaultY());
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) pos.x, (float) pos.y);
            context.getMatrices().scale(pos.scale, pos.scale);
            el.render(context, mc, 0, 0);
            context.getMatrices().popMatrix();
        }

        if (keystrokesElement.isEnabled()) {
            Config.ElementPos pos = Config.getPos(
                    keystrokesElement.getId(),
                    keystrokesElement.getDefaultX(mc),
                    keystrokesElement.getDefaultY(mc)
            );
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) pos.x, (float) pos.y);
            context.getMatrices().scale(pos.scale, pos.scale);
            keystrokesElement.render(context, mc, 0, 0);
            context.getMatrices().popMatrix();
        }

        if (Config.showArmor && armorElement.isEnabled()) {
            int scaledW = mc.getWindow().getScaledWidth();
            int scaledH = mc.getWindow().getScaledHeight();
            int armorW = armorElement.getWidth(mc);
            int armorH = armorElement.getHeight(mc);
            int ax = (scaledW / 2) - 91 - 29 - armorW - 6;
            int ay = (scaledH - 11) - (armorH / 2);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) ax, (float) ay);
            armorElement.render(context, mc, 0, 0);
            context.getMatrices().popMatrix();
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