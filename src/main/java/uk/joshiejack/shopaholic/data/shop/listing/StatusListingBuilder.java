package uk.joshiejack.shopaholic.data.shop.listing;

import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.shopaholic.data.shop.comparator.ComparatorBuilder;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.PlayerStatusListing;
import uk.joshiejack.shopaholic.world.shop.listing.TeamStatusListing;

@SuppressWarnings("unused")
public class StatusListingBuilder extends SublistingBuilder<Pair<String, ComparatorBuilder>> {
    private final String type;
    public StatusListingBuilder(String type, String field, ComparatorBuilder comparator) {
        super(Pair.of(field, comparator));
        this.type = type;
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                type.equals("player") ? new PlayerStatusListing(data.getLeft(), data.getRight().build()) : new TeamStatusListing(data.getLeft(), data.getRight().build()),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}

