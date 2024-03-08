package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.world.shop.input.EntityShopInput;
import uk.joshiejack.shopaholic.world.shop.input.InputMethod;
import uk.joshiejack.shopaholic.world.shop.input.InputToShop;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class OpenShopCommand {
    public static final SuggestionProvider<CommandSourceStack> ALL_SHOPS = SuggestionProviders.register(
            new ResourceLocation(Shopaholic.MODID, "all_shops"),
            (ctx, builder) -> SharedSuggestionProvider.suggest(suggestions(ctx), builder)
    );

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("open")
                .then(Commands.argument("shop", StringArgumentType.string())
                        .suggests(ALL_SHOPS)
                        .executes(ctx -> {
                            String arg = StringArgumentType.getString(ctx, "shop");
                            Player player = ctx.getSource().getPlayerOrException();
                            return InputToShop.open(InputToShop.COMMAND_TO_SHOP.get(arg),
                                    new ShopTarget(player.level(), player.blockPosition(), player, player, ItemStack.EMPTY, new EntityShopInput(player)),
                                    InputMethod.COMMAND) ? 1 : 0;
                        }));
    }

    private static Stream<String> suggestions(final CommandContext<SharedSuggestionProvider> context) {
        if (context.getSource() instanceof CommandSourceStack stack) {
            return InputToShop.COMMAND_TO_SHOP.entries().stream()
                    .filter(e -> e.getValue().isValidFor(ShopTarget.fromSource(stack), Condition.CheckType.SHOP_EXISTS))
                    .map(Map.Entry::getKey);
        } else return Stream.empty();
    }

    public static class ShopSuggestions implements Consumer<CommandSourceStack> {
        @Override
        public void accept(CommandSourceStack commandSourceStack) {

        }
    }
}
