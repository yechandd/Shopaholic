package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public class VendorPositionComparator {
    public record X() implements Comparator {
        public static final X INSTANCE = new X();
        public static final Codec<X> CODEC = Codec.unit(INSTANCE);

        @Override
        public Codec<? extends Comparator> codec() {
            return CODEC;
        }

        @Override
        public int getValue(@NotNull ShopTarget target) {
            return target.getPos().getX();
        }
    }

    public record Y() implements Comparator {
        public static final Y INSTANCE = new Y();
        public static final Codec<Y> CODEC = Codec.unit(INSTANCE);

        @Override
        public Codec<? extends Comparator> codec() {
            return CODEC;
        }

        @Override
        public int getValue(@NotNull ShopTarget target) {
            return target.getPos().getY();
        }
    }

    public record Z() implements Comparator {
        public static final Z INSTANCE = new Z();
        public static final Codec<Z> CODEC = Codec.unit(INSTANCE);

        @Override
        public Codec<? extends Comparator> codec() {
            return CODEC;
        }

        @Override
        public int getValue(@NotNull ShopTarget target) {
            return target.getPos().getZ();
        }
    }
}