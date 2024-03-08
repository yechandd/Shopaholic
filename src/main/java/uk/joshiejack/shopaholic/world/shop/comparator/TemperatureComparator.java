package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record TemperatureComparator() implements Comparator {
    public static final TemperatureComparator INSTANCE = new TemperatureComparator();
    public static final Codec<TemperatureComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getValue(@NotNull ShopTarget target) {
        return (int) target.getLevel().getBiome(target.getPos()).value().getTemperature(target.getPos());
    }
}