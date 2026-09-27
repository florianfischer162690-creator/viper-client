package com.viper.client.mixin;

import com.viper.client.config.Config;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
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
public class ShulkerPreviewMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void viper_shulkerTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        try {
            if (!Config.showShulkerPreview) return;
            ItemStack stack = (ItemStack) (Object) this;
            ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
            if (container == null) return;

            // sammle items
            java.util.List<ItemStack> items = new java.util.ArrayList<>();
            for (ItemStack item : container.iterateNonEmpty()) {
                items.add(item);
            }
            if (items.isEmpty()) return;

            textConsumer.accept(Text.literal(""));
            textConsumer.accept(
                    Text.literal("━━━ Contents (" + items.size() + ") ━━━")
                            .formatted(Formatting.LIGHT_PURPLE)
            );
            for (ItemStack item : items) {
                Text line = Text.literal(" • ")
                        .formatted(Formatting.DARK_GRAY)
                        .append(Text.literal(item.getCount() + "x ").formatted(Formatting.GRAY))
                        .append(item.getName().copy().formatted(Formatting.WHITE));
                textConsumer.accept(line);
            }
        } catch (Throwable ignored) {}
    }
}