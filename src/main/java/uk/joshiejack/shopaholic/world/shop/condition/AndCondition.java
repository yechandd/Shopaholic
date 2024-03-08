package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public record AndCondition(List<Condition> conditions) implements Condition {
    public static final Codec<AndCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(condition -> condition.conditions)
    ).apply(instance, AndCondition::new));

    @SuppressWarnings("unused")
    public static Condition and(Condition... conditions) {
        return new AndCondition(Arrays.stream(conditions).toList());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return conditions.stream().allMatch(condition -> condition.valid(target, type));
    }
}
