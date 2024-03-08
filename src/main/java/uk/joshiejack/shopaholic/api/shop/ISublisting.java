package uk.joshiejack.shopaholic.api.shop;

import net.minecraft.world.item.ItemStack;

public interface ISublisting {
    /**
     * @return the cost of this item
     */
    long getGold();

    /**
     * returns the listing as an itemstack
     * empty if it's not an item
     * @return the item
     */
    ItemStack getItem();
}
