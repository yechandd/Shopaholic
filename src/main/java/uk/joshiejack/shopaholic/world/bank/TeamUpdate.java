package uk.joshiejack.shopaholic.world.bank;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.event.TeamChangedEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shipping.Market;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class TeamUpdate {
    @SubscribeEvent
    public static void onTeamChanged(TeamChangedEvent event) {
        ServerPlayer player = (ServerPlayer) event.getLevel().getPlayerByUUID(event.getPlayer());
        if (player != null) {
            Bank.get((ServerLevel) event.getLevel()).syncToPlayer(player);
            Market.get((ServerLevel) event.getLevel()).getShippingForPlayer(player).syncToPlayer(player);
        }
    }
}
