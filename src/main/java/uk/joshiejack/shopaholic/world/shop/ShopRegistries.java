package uk.joshiejack.shopaholic.world.shop;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import uk.joshiejack.shopaholic.api.shop.*;

import java.util.Map;

public class ShopRegistries {
    public static final BiMap<String, Comparator> COMPARATORS = HashBiMap.create();
    public static final Map<String, Condition> CONDITIONS = new Object2ObjectOpenHashMap<>();
    public static final Map<String, CostFormula> COST_FORMULAE = new Object2ObjectOpenHashMap<>();
    public static final BiMap<String, ListingType.Builder> LISTING_TYPES = HashBiMap.create();
}
