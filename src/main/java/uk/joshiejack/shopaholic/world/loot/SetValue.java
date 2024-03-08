package uk.joshiejack.shopaholic.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

import javax.annotation.Nonnull;
import java.util.List;

public class SetValue extends LootItemConditionalFunction {
    public static final Codec<SetValue> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ExtraCodecs.strictOptionalField(LootItemConditions.CODEC.listOf(), "conditions", List.of()).forGetter(inst -> inst.predicates),
                            Codec.LONG.fieldOf("days").forGetter(inst -> inst.value))
                    .apply(instance, SetValue::new)
    );

    private final long value;

    public SetValue(List<LootItemCondition> conditions, long value) {
        super(conditions);
        this.value = value;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ShopaholicLoot.RATIO_VALUE.value();
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        assert stack.getTag() != null;
        stack.getTag().putLong("SellValue", value);
        return stack;
    }
}
