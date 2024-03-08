package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record TeamStatusComparator(String key) implements Comparator {
    public static final Codec<TeamStatusComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(comparator -> comparator.key)
    ).apply(instance, TeamStatusComparator::new));


    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return PenguinTeams.getPenguinStatuses(target.getPlayer()).getInt(key);
    }
}