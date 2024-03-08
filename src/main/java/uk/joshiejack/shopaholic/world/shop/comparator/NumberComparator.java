package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record NumberComparator(int value) implements Comparator {
    public static final Codec<NumberComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("days").forGetter(NumberComparator::value)
    ).apply(instance, NumberComparator::new));

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return value;
    }
}