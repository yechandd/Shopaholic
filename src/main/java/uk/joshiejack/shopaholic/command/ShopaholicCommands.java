package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import uk.joshiejack.shopaholic.Shopaholic;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class ShopaholicCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(Shopaholic.MODID)
                        .then(GoldCommand.register().requires(cs -> cs.hasPermission(2)))
                        .then(ShipCommand.register())
                        .then(OpenShopCommand.register())
                        .then(ManagerCommand.register())
                        .then(ValueCommand.register())
        );
    }
}
