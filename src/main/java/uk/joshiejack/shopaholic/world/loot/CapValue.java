package uk.joshiejack.shopaholic.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import uk.joshiejack.shopaholic.world.shipping.ShippingRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class CapValue extends LootItemConditionalFunction {
    public static final Codec<CapValue> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ExtraCodecs.strictOptionalField(LootItemConditions.CODEC.listOf(), "conditions", List.of()).forGetter(p_299114_ -> p_299114_.predicates),
                    Codec.LONG.fieldOf("max").forGetter(inst -> inst.cap))
                    .apply(instance, CapValue::new)
    );

    private final long cap;

    public CapValue(List<LootItemCondition> conditions, long max) {
        super(conditions);
        this.cap = max;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ShopaholicLoot.CAP_VALUE.value();
    }

    @Nonnull
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        long value = ShippingRegistry.getValue(stack);
        if (value > cap && !stack.hasTag()) {
            stack.setTag(new CompoundTag());
            stack.getTag().putLong("SellValue", cap);
        }

        return stack;
    }
}
