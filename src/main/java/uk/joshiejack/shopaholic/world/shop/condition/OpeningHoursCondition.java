package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public record OpeningHoursCondition(Map<Weekday, Pair<Integer, Integer>> hours) implements Condition {
    public enum Weekday implements StringRepresentable {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final Codec<Weekday> DAY_OF_WEEK_CODEC = StringRepresentable.fromEnum(Weekday::values);
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
        protected final Map<Weekday, Pair<Integer, Integer>> hours = new HashMap<>();
        public Condition build() {
            return new OpeningHoursCondition(hours);
        }

        public Builder withHours(Weekday day, int opening, int closing) {
            hours.put(day, Pair.of(opening, closing));
            return this;
        }

        public Builder withHours(DayOfWeek day, int opening, int closing) {
            return withHours(Weekday.valueOf(day.name()), opening, closing);
        }
    }
}
