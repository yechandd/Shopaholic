package uk.joshiejack.shopaholic.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

public class ItemPurchasedEvent extends PlayerEvent {
    private final Department shop;
    private final Listing purchasable;

    public ItemPurchasedEvent(Player player, Department shop, Listing purchasable) {
        super(player);
        this.shop = shop;
        this.purchasable = purchasable;
    }

    public Department getShop() {
        return shop;
    }

    public Listing getListing() {
        return purchasable;
    }
}
