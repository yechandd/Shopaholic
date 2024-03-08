package uk.joshiejack.shopaholic.api.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.world.shop.ShopRegistries;

import javax.annotation.Nonnull;

public interface Comparator {
    public static final Codec<Comparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("comparator").forGetter(c -> ShopRegistries.COMPARATORS.inverse().get(c))
    ).apply(instance, ShopRegistries.COMPARATORS::get));

        //Codec.STRING.xmap(Comparator::new, (comparator) -> ShopRegistries.COMPARATORS.inverse().get(comparator));

    Codec<? extends Comparator> codec();

    /**
     * Return the days of this comparator
     * Often things can be just true (1) or (0) false
     * @param target    the target object
     * @return  the days this is considered to have
     */
    int getValue(@Nonnull ShopTarget target);
}
