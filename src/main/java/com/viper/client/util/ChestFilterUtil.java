package com.viper.client.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashSet;
import java.util.Set;

public class ChestFilterUtil {

    private static final Set<net.minecraft.item.Item> VALUABLE = new HashSet<>();

    static {
        // diamanten zeug
        VALUABLE.add(Items.DIAMOND);
        VALUABLE.add(Items.DIAMOND_BLOCK);
        VALUABLE.add(Items.DIAMOND_SWORD);
        VALUABLE.add(Items.DIAMOND_PICKAXE);
        VALUABLE.add(Items.DIAMOND_AXE);
        VALUABLE.add(Items.DIAMOND_SHOVEL);
        VALUABLE.add(Items.DIAMOND_HOE);
        VALUABLE.add(Items.DIAMOND_HELMET);
        VALUABLE.add(Items.DIAMOND_CHESTPLATE);
        VALUABLE.add(Items.DIAMOND_LEGGINGS);
        VALUABLE.add(Items.DIAMOND_BOOTS);

        // netherite
        VALUABLE.add(Items.NETHERITE_INGOT);
        VALUABLE.add(Items.NETHERITE_BLOCK);
        VALUABLE.add(Items.NETHERITE_SWORD);
        VALUABLE.add(Items.NETHERITE_PICKAXE);
        VALUABLE.add(Items.NETHERITE_AXE);
        VALUABLE.add(Items.NETHERITE_SHOVEL);
        VALUABLE.add(Items.NETHERITE_HOE);
        VALUABLE.add(Items.NETHERITE_HELMET);
        VALUABLE.add(Items.NETHERITE_CHESTPLATE);
        VALUABLE.add(Items.NETHERITE_LEGGINGS);
        VALUABLE.add(Items.NETHERITE_BOOTS);

        // gold / iron
        VALUABLE.add(Items.GOLD_INGOT);
        VALUABLE.add(Items.GOLD_BLOCK);
        VALUABLE.add(Items.IRON_INGOT);
        VALUABLE.add(Items.IRON_BLOCK);

        // selten
        VALUABLE.add(Items.EMERALD);
        VALUABLE.add(Items.EMERALD_BLOCK);
        VALUABLE.add(Items.ENCHANTED_GOLDEN_APPLE);
        VALUABLE.add(Items.GOLDEN_APPLE);
        VALUABLE.add(Items.NETHER_STAR);
        VALUABLE.add(Items.ELYTRA);
        VALUABLE.add(Items.TOTEM_OF_UNDYING);
        VALUABLE.add(Items.SHULKER_BOX);
        VALUABLE.add(Items.NETHERITE_SCRAP);

        // spawner
        VALUABLE.add(Items.SPAWNER);

        // elytra + trident + crossbow
        VALUABLE.add(Items.TRIDENT);
        VALUABLE.add(Items.CROSSBOW);

        // books
        VALUABLE.add(Items.ENCHANTED_BOOK);
        VALUABLE.add(Items.EXPERIENCE_BOTTLE);
    }

    public static boolean isValuable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return VALUABLE.contains(stack.getItem());
    }
}