package com.viper.client.gui;

import com.viper.client.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ViperConfigScreen extends Screen {

    private final Screen parent;

    public ViperConfigScreen(Screen parent) {
        super(Text.literal("Viper V1 — Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 50;
        int spacing = 22;

        addToggle(cx, y, "HUD", () -> Config.hudEnabled, v -> Config.hudEnabled = v);
        y += spacing;

        addToggle(cx, y, "Show FPS", () -> Config.showFps, v -> Config.showFps = v);
        y += spacing;

        addToggle(cx, y, "Show CPS", () -> Config.showCps, v -> Config.showCps = v);
        y += spacing;

        addToggle(cx, y, "Show Coords", () -> Config.showCoords, v -> Config.showCoords = v);
        y += spacing;

        addToggle(cx, y, "Show Ping", () -> Config.showPing, v -> Config.showPing = v);
        y += spacing;

        addToggle(cx, y, "Show Armor", () -> Config.showArmor, v -> Config.showArmor = v);
        y += spacing;

        addToggle(cx, y, "Show Potions", () -> Config.showPotion, v -> Config.showPotion = v);
        y += spacing;

        addToggle(cx, y, "Show Keystrokes", () -> Config.showKeystrokes, v -> Config.showKeystrokes = v);
        y += spacing;

        addToggle(cx, y, "Toggle Sprint", () -> Config.toggleSprint, v -> Config.toggleSprint = v);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                btn -> {
                    Config.save();
                    this.client.setScreen(parent);
                }
        ).dimensions(cx - 60, this.height - 40, 120, 20).build());
    }

    private void addToggle(int cx, int y, String label, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
        this.addDrawableChild(ButtonWidget.builder(
                buildLabel(label, getter.get()),
                btn -> {
                    boolean newVal = !getter.get();
                    setter.accept(newVal);
                    btn.setMessage(buildLabel(label, newVal));
                }
        ).dimensions(cx - 100, y, 200, 20).build());
    }

    private Text buildLabel(String name, boolean value) {
        return Text.literal(name + ": ")
                .append(Text.literal(value ? "ON" : "OFF")
                        .formatted(value ? Formatting.GREEN : Formatting.RED));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("VIPER V1").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                this.width / 2,
                15,
                0xFFA855F7
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Settings").formatted(Formatting.GRAY),
                this.width / 2,
                30,
                0xFF8A8A9C
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        Config.save();
        this.client.setScreen(parent);
    }
}