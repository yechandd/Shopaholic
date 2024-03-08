package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record PlayerStatusComparator(String key) implements Comparator {
    public static final Codec<PlayerStatusComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(comparator -> comparator.key)
    ).apply(instance, PlayerStatusComparator::new));


    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return PlayerHelper.getPenguinStatuses(target.getPlayer()).getInt(key);
    }
}