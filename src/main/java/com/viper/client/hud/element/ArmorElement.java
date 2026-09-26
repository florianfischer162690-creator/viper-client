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

    private static final int VANILLA_BAR_BG = 0xFF000000;
    private static final int VANILLA_BAR_FG_GOOD = 0xFF00FF00;
    private static final int VANILLA_BAR_FG_MID  = 0xFFFFFF00;
    private static final int VANILLA_BAR_FG_LOW  = 0xFFFF0000;

    @Override
    public String getId() { return "armor"; }

    @Override
    public boolean isEnabled() { return Config.showArmor; }

    @Override
    public int getDefaultX() { return 4; }
    @Override
    public int getDefaultY() { return 76; }

    @Override
    public int render(DrawContext context, MinecraftClient mc, int x, int y) {
        if (mc.player == null) return 0;

        int accent = Config.getAccent();
        int accentLight = Config.getAccentLight();
        int accentDark = Config.getAccentDark();

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

            drawSlot(context, slotX, y, SLOT_SIZE, accent, accentLight, accentDark);

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

    private void drawSlot(DrawContext context, int x, int y, int size, int accent, int accentLight, int accentDark) {
        // außen-rahmen in accent-dark
        context.fill(x, y, x + size, y + size, accent);
        // innerer slot (dunkel)
        context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF1c1228);
        // highlight oben-links (hellere akzent-variante)
        int highlight = (accentLight & 0x00FFFFFF) | 0x66000000;
        context.fill(x + 1, y + 1, x + size - 1, y + 2, highlight);
        context.fill(x + 1, y + 1, x + 2, y + size - 1, highlight);
        // shadow unten-rechts (dunkler akzent)
        context.fill(x + 1, y + size - 2, x + size - 1, y + size - 1, accentDark);
        context.fill(x + size - 2, y + 1, x + size - 1, y + size - 1, accentDark);
    }

    @Override
    public int getWidth(MinecraftClient mc) {
        return SLOT_SIZE * 4 + SLOT_GAP * 3;
    }

    @Override
    public int getHeight(MinecraftClient mc) { return SLOT_SIZE; }
}