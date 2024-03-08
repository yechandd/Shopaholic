package uk.joshiejack.shopaholic.plugin.simplyseasons.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SeasonCondition implements Condition {
    public static final Codec<SeasonCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Season.CODEC.fieldOf("season").forGetter(condition -> condition.season)
    ).apply(instance, SeasonCondition::new));
    private Season season;

    public SeasonCondition() {}
    public SeasonCondition(String season) {
        this.season = Season.valueOf(season.toUpperCase());
    }
    public SeasonCondition(Season season) {
        this.season = season;
    }

    @SuppressWarnings("unused")
    public static Condition inSeason(String season) {
        return new SeasonCondition(season);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(target.getLevel().dimension());
        return provider.isPresent() && provider.get().getSeason(target.getLevel()) == season;
    }
}