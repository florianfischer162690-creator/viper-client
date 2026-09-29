package com.viper.client.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ItemPrices {

    private static final Map<Item, Long> PRICES = new HashMap<>();

    static {
        // HugoSMP preise (du kannst die anpassen)
        PRICES.put(Items.DIAMOND, 100L);
        PRICES.put(Items.DIAMOND_BLOCK, 900L);
        PRICES.put(Items.NETHERITE_INGOT, 5000L);
        PRICES.put(Items.NETHERITE_BLOCK, 45000L);
        PRICES.put(Items.NETHERITE_SCRAP, 1500L);
        PRICES.put(Items.IRON_INGOT, 20L);
        PRICES.put(Items.IRON_BLOCK, 180L);
        PRICES.put(Items.GOLD_INGOT, 25L);
        PRICES.put(Items.GOLD_BLOCK, 225L);
        PRICES.put(Items.EMERALD, 50L);
        PRICES.put(Items.EMERALD_BLOCK, 450L);
        PRICES.put(Items.ENCHANTED_GOLDEN_APPLE, 10000L);
        PRICES.put(Items.GOLDEN_APPLE, 500L);
        PRICES.put(Items.NETHER_STAR, 100000L);
        PRICES.put(Items.ELYTRA, 50000L);
        PRICES.put(Items.TOTEM_OF_UNDYING, 25000L);
        PRICES.put(Items.SHULKER_BOX, 3000L);
        PRICES.put(Items.SPAWNER, 50000L);
        PRICES.put(Items.TRIDENT, 20000L);
        PRICES.put(Items.EXPERIENCE_BOTTLE, 100L);
        PRICES.put(Items.ENCHANTED_BOOK, 500L);
        PRICES.put(Items.NETHERITE_SWORD, 10000L);
        PRICES.put(Items.NETHERITE_PICKAXE, 10000L);
        PRICES.put(Items.NETHERITE_HELMET, 8000L);
        PRICES.put(Items.NETHERITE_CHESTPLATE, 12000L);
        PRICES.put(Items.NETHERITE_LEGGINGS, 10000L);
        PRICES.put(Items.NETHERITE_BOOTS, 8000L);
        PRICES.put(Items.DIAMOND_SWORD, 200L);
        PRICES.put(Items.DIAMOND_PICKAXE, 200L);
        PRICES.put(Items.DIAMOND_HELMET, 300L);
        PRICES.put(Items.DIAMOND_CHESTPLATE, 500L);
        PRICES.put(Items.DIAMOND_LEGGINGS, 400L);
        PRICES.put(Items.DIAMOND_BOOTS, 300L);
        PRICES.put(Items.ANCIENT_DEBRIS, 2000L);
        PRICES.put(Items.OBSIDIAN, 5L);
        PRICES.put(Items.CRYING_OBSIDIAN, 15L);
        PRICES.put(Items.END_CRYSTAL, 30L);
        PRICES.put(Items.RESPAWN_ANCHOR, 100L);
        PRICES.put(Items.GLOWSTONE, 10L);
        PRICES.put(Items.ENDER_PEARL, 20L);
        PRICES.put(Items.ENDER_EYE, 50L);
        PRICES.put(Items.GHAST_TEAR, 500L);
        PRICES.put(Items.BLAZE_ROD, 100L);
        PRICES.put(Items.NETHER_WART, 20L);
        PRICES.put(Items.SUGAR_CANE, 5L);
    }

    public static long getPrice(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        Long p = PRICES.get(stack.getItem());
        if (p == null) return 0L;
        return p * stack.getCount();
    }

    public static long getUnitPrice(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        Long p = PRICES.get(stack.getItem());
        return p == null ? 0L : p;
    }

    public static boolean hasPrice(ItemStack stack) {
        return stack != null && !stack.isEmpty() && PRICES.containsKey(stack.getItem());
    }
}