package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record CompareCondition(Comparator comparator1, boolean lessThan, boolean equalTo, boolean greaterThan, Comparator comparator2) implements Condition {
    public static final Codec<CompareCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Comparator.CODEC.fieldOf("comparator 1").forGetter(validator -> validator.comparator1),
            Codec.BOOL.optionalFieldOf("equal_to", true).forGetter(validator -> validator.lessThan),
            Codec.BOOL.optionalFieldOf("less_than", true).forGetter(validator -> validator.equalTo),
            Codec.BOOL.optionalFieldOf("greater_than", true).forGetter(validator -> validator.greaterThan),
            Comparator.CODEC.fieldOf("comparator 2").forGetter(validator -> validator.comparator2)
    ).apply(instance, CompareCondition::new));

    @SuppressWarnings("unused")
    public static Condition compare(Comparator compare1, boolean lessThan, boolean equalTo, boolean greaterThan, Comparator compare2) {
        return new CompareCondition(compare1, lessThan, equalTo, greaterThan, compare2);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        if (comparator1 == null || comparator2 == null) return false;
        int value1 = comparator1.getValue(target);
        int value2 = comparator2.getValue(target);
        return ((greaterThan && value1 > value2) || (lessThan && value1 < value2) || (equalTo && value1 == value2));
    }
}
