package uk.joshiejack.shopaholic.api.shop;

import com.mojang.serialization.Codec;

import javax.annotation.Nonnull;

public interface Comparator {
    Codec<? extends Comparator> codec();

    /**
     * Return the days of this comparator
     * Often things can be just true (1) or (0) false
     * @param target    the target object
     * @return  the days this is considered to have
     */
    int getValue(@Nonnull ShopTarget target);
}
