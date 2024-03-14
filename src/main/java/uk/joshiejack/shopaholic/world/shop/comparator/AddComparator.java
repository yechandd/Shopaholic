package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.List;

public record AddComparator(List<Comparator> comparators) implements Comparator {
    public static final Codec<AddComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Shopaholic.ShopaholicRegistries.COMPARATOR_CODEC.listOf().fieldOf("comparators").forGetter(inst -> inst.comparators)
    ).apply(instance, AddComparator::new));

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    public static Comparator add(Comparator ...comparators) {
        return new AddComparator(List.of(comparators));
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return comparators.stream().mapToInt(c -> c.getValue(target)).sum();
    }
}