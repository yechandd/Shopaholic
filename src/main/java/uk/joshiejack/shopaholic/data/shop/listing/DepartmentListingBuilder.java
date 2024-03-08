package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.DepartmentListing;

public class DepartmentListingBuilder extends SublistingBuilder<ResourceLocation> {
    public DepartmentListingBuilder(ResourceLocation departmentID) {
        super(departmentID);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new DepartmentListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
