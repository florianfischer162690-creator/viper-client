package com.viper.client.hud.element;

import com.viper.client.config.Config;
import com.viper.client.hud.HudRenderer.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorElement extends HudElement {

    private static final int SLOT_SIZE = 20;
    private static final int SLOT_GAP = 1;

    // Viper lila slot-farben
    private static final int SLOT_BORDER = 0xFFA855F7;   // lila rahmen
    private static final int SLOT_INNER  = 0xFF1c1228;   // dunkel-lila innen
    private static final int SLOT_HL     = 0x66c084fc;   // highlight oben-links (hell-lila, halbtransparent)
    private static final int SLOT_SH     = 0xFF3a2050;   // schatten unten-rechts (dunkler lila)

    private static final int VANILLA_BAR_BG = 0xFF000000;
    private static final int VANILLA_BAR_FG_GOOD = 0xFF00FF00;
    private static final int VANILLA_BAR_FG_MID  = 0xFFFFFF00;
    private static final int VANILLA_BAR_FG_LOW  = 0xFFFF0000;

    @Override
    public String getId() { return "armor"; }

    @Override
    public boolean isEnabled() {
        return Config.showArmor;
    }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 76; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;

        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        int visible = 0;
        for (EquipmentSlot s : slots) {
            if (!mc.player.getEquippedStack(s).isEmpty()) visible++;
        }
        if (visible == 0) return 0;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(slots[i]);
            int slotX = x + i * (SLOT_SIZE + SLOT_GAP);

            drawSlot(context, slotX, y, SLOT_SIZE);

            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX + 2, y + 2);

                if (stack.isDamageable() && stack.getMaxDamage() > 0) {
                    int max = stack.getMaxDamage();
                    int dmg = stack.getDamage();
                    float ratio = 1.0f - ((float) dmg / max);
                    if (ratio < 0) ratio = 0;
                    if (ratio > 1) ratio = 1;

                    int barY = y + 15;
                    int barW = 13;
                    int barX = slotX + 3;

                    context.fill(barX, barY, barX + barW, barY + 2, VANILLA_BAR_BG);

                    int fillW = Math.round(barW * ratio);
                    if (fillW > 0) {
                        int color;
                        if (ratio > 0.75f) color = VANILLA_BAR_FG_GOOD;
                        else if (ratio > 0.5f) color = VANILLA_BAR_FG_MID;
                        else color = VANILLA_BAR_FG_LOW;

                        context.fill(barX, barY, barX + fillW, barY + 2, color);
                    }
                }
            }
        }

        return SLOT_SIZE;
    }

    /** lila slot-rahmen im vanilla-style */
    private void drawSlot(DrawContext context, int x, int y, int size) {
        // lila außen-rahmen
        context.fill(x, y, x + size, y + size, SLOT_BORDER);
        // dunkel-lila innen
        context.fill(x + 1, y + 1, x + size - 1, y + size - 1, SLOT_INNER);
        // highlight oben-links
        context.fill(x + 1, y + 1, x + size - 1, y + 2, SLOT_HL);
        context.fill(x + 1, y + 1, x + 2, y + size - 1, SLOT_HL);
        // schatten unten-rechts
        context.fill(x + 1, y + size - 2, x + size - 1, y + size - 1, SLOT_SH);
        context.fill(x + size - 2, y + 1, x + size - 1, y + size - 1, SLOT_SH);
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return SLOT_SIZE * 4 + SLOT_GAP * 3;
    }

    @Override
    public int getHeight(MinecraftClient mc) {
        return SLOT_SIZE;
    }
}