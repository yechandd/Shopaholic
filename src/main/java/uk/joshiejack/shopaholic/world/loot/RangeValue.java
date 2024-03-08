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

import javax.annotation.Nonnull;
import java.util.List;

public class RangeValue extends LootItemConditionalFunction {
    public static final Codec<RangeValue> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ExtraCodecs.strictOptionalField(LootItemConditions.CODEC.listOf(), "conditions", List.of()).forGetter(inst -> inst.predicates),
                            Codec.LONG.fieldOf("min").forGetter(inst -> inst.minValue),
                            Codec.LONG.fieldOf("max").forGetter(inst -> inst.maxValue))
                    .apply(instance, RangeValue::new)
    );

    private final long minValue;
    private final long maxValue;

    public RangeValue(List<LootItemCondition> conditions, long min, long max) {
        super(conditions);
        this.minValue = min;
        this.maxValue = max;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ShopaholicLoot.RANGE_VALUE.value();
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putLong("SellValue", minValue != maxValue ? context.getRandom().nextInt((int) (maxValue-minValue)) + minValue : minValue);
        return stack;
    }
}
