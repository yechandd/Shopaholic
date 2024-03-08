package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record PlayerHealthComparator() implements Comparator {
    public static final PlayerHealthComparator INSTANCE = new PlayerHealthComparator();
    public static final Codec<PlayerHealthComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return (int) target.getPlayer().getHealth();
    }
}