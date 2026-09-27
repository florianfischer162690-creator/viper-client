package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
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
public class FoodHoverMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void viper_foodTooltip(Item.TooltipContext context, TooltipDisplayComponent tooltipDisplay, PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        try {
            if (!Config.showAppleskin) return;
            ItemStack stack = (ItemStack) (Object) this;
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food == null) return;

            int hunger = food.nutrition();
            float sat = food.saturation();

            textConsumer.accept(Text.literal(""));
            textConsumer.accept(
                    Text.literal("🍗 Hunger: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(String.valueOf(hunger)).formatted(Formatting.GREEN))
            );
            textConsumer.accept(
                    Text.literal("✨ Saturation: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(String.format("%.1f", sat)).formatted(Formatting.GOLD))
            );
        } catch (Throwable ignored) {}
    }
}