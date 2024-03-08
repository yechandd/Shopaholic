package uk.joshiejack.shopaholic.data.shop.listing;

import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.CommandListing;

public class CommandListingBuilder extends SublistingBuilder<String> {
    @SuppressWarnings("unused")
    public CommandListingBuilder(String command) {
        super(command);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new CommandListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
