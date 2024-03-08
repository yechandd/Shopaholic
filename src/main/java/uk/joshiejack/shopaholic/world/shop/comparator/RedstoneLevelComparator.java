package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record RedstoneLevelComparator() implements Comparator {
    public static final RedstoneLevelComparator INSTANCE = new RedstoneLevelComparator();
    public static final Codec<RedstoneLevelComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return target.getLevel().getDirectSignalTo(target.getPos());
    }
}