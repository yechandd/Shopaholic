package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;

public class ValueCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("value")
                .then(Commands.literal("remove")
                        .executes(ctx -> {
                            ItemStack held = ctx.getSource().getPlayerOrException().getMainHandItem();
                            if (held.hasTag() &&  held.getTag().contains("SellValue")) {
                                held.getTag().remove("SellValue");

                                //If the tag is empty, remove it
                                if (held.getTag().isEmpty()) {
                                    held.setTag(null);
                                }

                                return 1;
                            }

                            return 0;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    ItemStack held = ctx.getSource().getPlayerOrException().getMainHandItem();
                                    held.getOrCreateTag().putLong("SellValue", IntegerArgumentType.getInteger(ctx, "amount"));
                                    return 1;
                                })));
    }
}
