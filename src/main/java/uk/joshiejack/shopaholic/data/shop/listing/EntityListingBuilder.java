package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.world.entity.EntityType;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.EntityTypeListing;

public class EntityListingBuilder extends SublistingBuilder<EntityType<?>> {
    public EntityListingBuilder(EntityType<?> entity) {
        super(entity);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new EntityTypeListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
