package uk.joshiejack.shopaholic.plugin.simplyseasons.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import javax.annotation.Nonnull;

public record SeasonDaysComparator(int days) implements Comparator {
    public static final Codec<SeasonDaysComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("days").forGetter(SeasonDaysComparator::days)
    ).apply(instance, SeasonDaysComparator::new));

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return (int) (days * CalendarDate.seasonLength(target.getLevel()));
    }
}