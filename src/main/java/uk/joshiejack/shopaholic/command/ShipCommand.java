package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.shopaholic.ShopaholicServerConfig;
import uk.joshiejack.shopaholic.world.shipping.Market;
import uk.joshiejack.shopaholic.world.shipping.Shipping;
import uk.joshiejack.shopaholic.world.shipping.ShippingRegistry;

public class ShipCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("ship").requires(ctx -> ShopaholicServerConfig.shipCommandEnabled.get())
                .then(Commands.literal("hand")
                        .executes(ctx -> {
                            ItemStack held = ctx.getSource().getPlayerOrException().getMainHandItem();
                            if (!held.isEmpty()) {
                                long value = ShippingRegistry.getValue(held);
                                if (value > 0) {
                                    Market.get(ctx.getSource().getLevel()).getShippingForPlayer(ctx.getSource().getPlayerOrException()).add(held.copy());
                                    Component name = held.getHoverName();
                                    held.shrink(held.getCount()); //Kill the item
                                    ctx.getSource().sendSuccess(() -> Component.translatable("command.shopaholic.ship.success", name), false);
                                    return 1;
                                }
                            }

                            ctx.getSource().sendFailure(Component.translatable("command.shopaholic.ship.no_value"));
                            return 0;
                        }))
                .then(Commands.literal("inventory")
                        .executes(ctx -> {
                            MutableInt successcount = new MutableInt();
                            MutableInt failcount = new MutableInt();
                            Shipping shipping = Market.get(ctx.getSource().getLevel()).getShippingForPlayer(ctx.getSource().getPlayerOrException());
                            PlayerHelper.getInventoryStream(ctx.getSource().getPlayerOrException()).forEach(stack -> {
                                if (!stack.isEmpty()) {
                                    long value = ShippingRegistry.getValue(stack);
                                    if (value > 0) {
                                        shipping.add(stack.copy());
                                        successcount.add(stack.getCount());
                                        stack.shrink(stack.getCount());
                                    } else failcount.add(stack.getCount());
                                }
                            });

                            ctx.getSource().sendSuccess(() -> Component.translatable("command.shopaholic.ship.success_many",
                                    successcount.intValue(), failcount.intValue()), false);
                            return successcount.intValue() > 0 ? 1 : 0;
                        }));
    }
}
