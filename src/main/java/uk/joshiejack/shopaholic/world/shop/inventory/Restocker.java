package uk.joshiejack.shopaholic.world.shop.inventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.event.NewDayEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shipping.Market;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class Restocker {
    @SubscribeEvent
    public static void onNewDay(NewDayEvent event) {
        ServerLevel world = event.getLevel();
        if (world.dimension() == Level.OVERWORLD) {
            Shopaholic.ShopaholicRegistries.DEPARTMENTS.registry().values().forEach(dp -> dp.getStockLevels(world).newDay(world.random));
            Inventory.setChanged(world);
            Market.get(event.getLevel()).newDay(world);
        }
    }
}
