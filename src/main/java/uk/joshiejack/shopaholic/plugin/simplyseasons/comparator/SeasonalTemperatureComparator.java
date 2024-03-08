package uk.joshiejack.shopaholic.plugin.simplyseasons.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

public record SeasonalTemperatureComparator() implements Comparator {
    public static final SeasonalTemperatureComparator INSTANCE = new SeasonalTemperatureComparator();
    public static final Codec<SeasonalTemperatureComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return (int) SeasonalWorlds.getTemperature(target.getLevel(), target.getLevel().getBiome(target.getPos()), target.getPos());
    }
}