package uk.joshiejack.shopaholic.plugin;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.plugin.simplyseasons.comparator.SeasonDaysComparator;
import uk.joshiejack.shopaholic.plugin.simplyseasons.comparator.SeasonalTemperatureComparator;
import uk.joshiejack.shopaholic.plugin.simplyseasons.condition.SeasonCondition;
import uk.joshiejack.shopaholic.plugin.simplyseasons.condition.SeasonPredicateCondition;
import uk.joshiejack.simplyseasons.client.SSClientConfig;

@Plugin("simplyseasons")
public class SimplySeasonsPlugin implements IModPlugin {
    public static final Holder<Codec<? extends Condition>> SEASON_PREDICATE = Shopaholic.ShopaholicRegistries.CONDITIONS.register("season_predicate", () -> SeasonPredicateCondition.CODEC);
    public static final Holder<Codec<? extends Condition>> SEASON = Shopaholic.ShopaholicRegistries.CONDITIONS.register("season", () -> SeasonCondition.CODEC);
    public static final Holder<Codec<? extends Comparator>> SEASON_DAYS = Shopaholic.ShopaholicRegistries.COMPARATORS.register("season_days", () -> SeasonDaysComparator.CODEC);
    public static final Holder<Codec<? extends Comparator>> TEMPERATURE = Shopaholic.ShopaholicRegistries.COMPARATORS.register("seasonal_temperature", () -> SeasonalTemperatureComparator.CODEC);

    public static boolean isHUDEnabled() {
        return SSClientConfig.enableHUD.get();
    }

    @Override
    public void setup() {}
}