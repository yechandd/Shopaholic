package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record TimeCondition(List<Pair<Integer, Integer>> times) implements Condition {
    public static final Codec<Pair<Integer, Integer>> TIME_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("open").forGetter(Pair::getLeft),
            Codec.INT.fieldOf("close").forGetter(Pair::getRight)
    ).apply(instance, Pair::of));
    public static final Codec<TimeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TIME_CODEC.listOf().fieldOf("times").forGetter(condition -> condition.times)
    ).apply(instance, TimeCondition::new));

    @SuppressWarnings("unused")
    public static Builder timeOpen(String id) {
        return Builder.create();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        if (type == CheckType.SHOP_EXISTS) return true; //Always true no matter the time
        for (Pair<Integer, Integer> time: times) {
            if (TimeHelper.isBetween(target.getLevel(), time.getLeft(), time.getRight())) return true;
        }

        return false;
    }

    public static class Builder {
        protected final List<Pair<Integer, Integer>> list = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public Builder withHours(int open, int close) {
            list.add(Pair.of(open, close));
            return this;
        }

        public Condition build() {
            return new TimeCondition(list);
        }
    }
}
