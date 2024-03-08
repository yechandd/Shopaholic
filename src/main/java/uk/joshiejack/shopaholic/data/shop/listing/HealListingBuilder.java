package uk.joshiejack.shopaholic.data.shop.listing;

import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.HealListing;

public class HealListingBuilder extends SublistingBuilder<Float> {
    public HealListingBuilder(float healAmount) {
        super(healAmount);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new HealListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
