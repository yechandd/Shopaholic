package uk.joshiejack.shopaholic.data.shop.listing;

import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.GoldListing;

public class GoldListingBuilder extends SublistingBuilder<Void> {
    public GoldListingBuilder() {
        super(null);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                GoldListing.INSTANCE,
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
