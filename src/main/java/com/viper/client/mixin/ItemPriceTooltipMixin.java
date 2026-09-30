package com.viper.client.mixin;

import com.viper.client.config.Config;
import com.viper.client.util.ItemPrices;
import com.viper.client.util.ServerTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemPriceTooltipMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void viper_itemPriceTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        try {
            if (!Config.itemPriceTooltip) return;
            if (!ServerTabs.showHugoTab()) return;

            ItemStack stack = (ItemStack) (Object) this;
            if (stack == null || stack.isEmpty()) return;

            if (!ItemPrices.hasPrice(stack)) return;

            long unit = ItemPrices.getUnitPrice(stack);
            long total = ItemPrices.getPrice(stack);

            textConsumer.accept(Text.literal(""));

            textConsumer.accept(
                    Text.literal("💰 Value: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(formatMoney(total)).formatted(Formatting.GOLD))
            );

            if (stack.getCount() > 1) {
                textConsumer.accept(
                        Text.literal("   Each: ")
                                .formatted(Formatting.DARK_GRAY)
                                .append(Text.literal(formatMoney(unit)).formatted(Formatting.YELLOW))
                );
            }
        } catch (Throwable ignored) {}
    }

    private String formatMoney(long amount) {
        if (amount >= 1000000L) return (amount / 1000000L) + "M";
        if (amount >= 1000L) return (amount / 1000L) + "K";
        return String.valueOf(amount);
    }
}