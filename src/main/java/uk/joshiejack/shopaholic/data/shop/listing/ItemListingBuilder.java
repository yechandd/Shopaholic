package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.world.item.ItemStack;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.ItemStackListing;

public class ItemListingBuilder extends SublistingBuilder<ItemStack> {
    public ItemListingBuilder(ItemStack item) {
        super(item);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new ItemStackListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
