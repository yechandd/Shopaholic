package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record OpeningHoursCondition(Map<DayOfWeek, Pair<Integer, Integer>> hours) implements Condition {
    public static final Codec<DayOfWeek> DAY_OF_WEEK_CODEC = Codec.STRING.xmap((d) -> DayOfWeek.valueOf(d.toUpperCase(Locale.ENGLISH)), (d) -> d.name().toLowerCase(Locale.ENGLISH));
    public static final Codec<OpeningHoursCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(DAY_OF_WEEK_CODEC, TimeCondition.TIME_CODEC).fieldOf("hours").forGetter(condition -> condition.hours)
    ).apply(instance, OpeningHoursCondition::new));

    @SuppressWarnings("unused")
    public static Builder openingHours() {
        return new Builder();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        if (type == CheckType.SHOP_EXISTS) return true; //Always add to the entity
        long time = target.getLevel().getDayTime();
        DayOfWeek weekday = TimeHelper.getWeekday(time);
        long timeOfDay = TimeHelper.getTimeOfDay(time);
        Pair<Integer, Integer> hours = this.hours.get(weekday);
        return hours != null && timeOfDay >= hours.getLeft() && timeOfDay <= hours.getRight();
    }

    //Conditions
    public static class Builder {
        protected final Map<DayOfWeek, Pair<Integer, Integer>> hours = new HashMap<>();
        public Condition build() {
            return new OpeningHoursCondition(hours);
        }

        public Builder withHours(DayOfWeek day, int opening, int closing) {
            return withHours(day, opening, closing);
        }
    }
}
