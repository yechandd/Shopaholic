package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.world.effect.MobEffectInstance;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.listing.MobEffectInstanceListing;

public class MobEffectListingBuilder extends SublistingBuilder<MobEffectInstance> {
    public MobEffectListingBuilder(MobEffectInstance effect) {
        super(effect);
    }

    @Override
    public Sublisting build() {
        return new Sublisting(id,
                new MobEffectInstanceListing(data),
                buildMaterials(),
                tooltip,
                icon,
                name,
                gold,
                weight);
    }
}
