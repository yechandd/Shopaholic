package uk.joshiejack.shopaholic.world.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.shopaholic.Shopaholic;

public class ShopaholicLoot {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Shopaholic.MODID);
    public static final Holder<LootItemFunctionType> CAP_VALUE = LOOT_FUNCTION_TYPES.register("cap_value", () -> new LootItemFunctionType(CapValue.CODEC));
    public static final Holder<LootItemFunctionType> RANGE_VALUE = LOOT_FUNCTION_TYPES.register("range_value", () -> new LootItemFunctionType(RangeValue.CODEC));
    public static final Holder<LootItemFunctionType> RATIO_VALUE = LOOT_FUNCTION_TYPES.register("ratio_value", () -> new LootItemFunctionType(RatioValue.CODEC));
    public static final Holder<LootItemFunctionType> SET_VALUE = LOOT_FUNCTION_TYPES.register("set_value", () -> new LootItemFunctionType(SetValue.CODEC));
}
