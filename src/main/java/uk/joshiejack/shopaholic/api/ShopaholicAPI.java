package uk.joshiejack.shopaholic.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.bank.IBank;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.CostFormula;
import uk.joshiejack.shopaholic.api.shop.ListingType;

@SuppressWarnings("ConstantConditions")
public class ShopaholicAPI {
    public static IRegistry registry = null;
    public static IBank bank = null;
    public static @NotNull Registry<Codec<? extends ListingType>> listingTypeRegistry() {
        return null;
    }
    public static @NotNull Registry<Codec<? extends Condition>> conditionRegistry() {
        return null;
    }

    public static @NotNull Registry<Codec<? extends Comparator>> comparatorRegistry() {
        return null;
    }

    public interface IRegistry {
        /**
         * Register a new cost formula
         * @param name          the name to register is as, used in the database
         * @param formula       the formula object
         * **/
        void registerCostFormula(String name, CostFormula formula);
    }
}
