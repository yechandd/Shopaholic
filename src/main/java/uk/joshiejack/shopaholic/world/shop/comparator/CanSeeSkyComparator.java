package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record CanSeeSkyComparator() implements Comparator {
    public static final CanSeeSkyComparator INSTANCE = new CanSeeSkyComparator();
    public static final Codec<CanSeeSkyComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return target.getLevel().canSeeSky(target.getPos()) ? 1 : 0;
    }
}