package uk.joshiejack.shopaholic.plugin.simplyseasons.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.simplyseasons.world.loot.SeasonPredicate;

import javax.annotation.Nonnull;

public class SeasonPredicateCondition implements Condition {
    public static final Codec<SeasonPredicateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SeasonPredicate.CODEC.fieldOf("season predicate").forGetter(condition -> condition.predicate)
    ).apply(instance, SeasonPredicateCondition::new));
    private SeasonPredicate predicate;

    public SeasonPredicateCondition() {}
    public SeasonPredicateCondition(String predicate) {
        this.predicate = SeasonPredicate.REGISTRY.get(predicate);
    }

    public SeasonPredicateCondition(SeasonPredicate predicate) {
        this.predicate = predicate;
    }

    @SuppressWarnings("unused")
    public static Condition seasonPredicate(String predicate) {
        return new SeasonPredicateCondition(predicate);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return predicate.matches(target.getLevel(), target.getPos());
    }
}
