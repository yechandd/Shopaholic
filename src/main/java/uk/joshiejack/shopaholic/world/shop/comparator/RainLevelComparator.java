package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record RainLevelComparator() implements Comparator {
    public static final RainLevelComparator INSTANCE = new RainLevelComparator();
    public static final Codec<RainLevelComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return target.getLevel().isThundering() && target.getLevel().isRaining() ? 2 : target.getLevel().isRaining() ? 1 : 0;
    }
}