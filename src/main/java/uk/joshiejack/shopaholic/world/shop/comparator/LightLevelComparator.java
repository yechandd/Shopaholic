package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record LightLevelComparator() implements Comparator {
    public static final LightLevelComparator INSTANCE = new LightLevelComparator();
    public static final Codec<LightLevelComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return target.getLevel().getLightEmission(target.getPos());
    }
}