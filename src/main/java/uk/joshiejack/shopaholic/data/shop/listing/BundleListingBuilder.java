package uk.joshiejack.shopaholic.data.shop.listing;

import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.BundleListing;

import java.util.ArrayList;
import java.util.List;

public class BundleListingBuilder extends SublistingBuilder<List<SublistingBuilder<?>>> {
    private final List<SublistingBuilder<?>> builders = new ArrayList<>();

    @SuppressWarnings("unused")
    public BundleListingBuilder() {
        super(new ArrayList<>());
    }

    @SuppressWarnings("unused")
    public BundleListingBuilder addToBundle(SublistingBuilder<?> builder) {
        this.builders.add(builder);
        return this;
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new BundleListing(builders.stream().map(s -> s.build().getProcessor()).toList()),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
