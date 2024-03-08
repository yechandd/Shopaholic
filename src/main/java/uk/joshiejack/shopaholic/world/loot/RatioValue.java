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

public class RatioValue extends LootItemConditionalFunction {
    public static final Codec<RatioValue> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ExtraCodecs.strictOptionalField(LootItemConditions.CODEC.listOf(), "conditions", List.of()).forGetter(inst -> inst.predicates),
                            Codec.DOUBLE.fieldOf("ratio").forGetter(inst -> inst.ratio))
                    .apply(instance, RatioValue::new)
    );

    private final double ratio;

    public RatioValue(List<LootItemCondition> conditions, double ratio) {
        super(conditions);
        this.ratio = ratio;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ShopaholicLoot.RATIO_VALUE.value();
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        long value = ShippingRegistry.getValue(stack);
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        assert stack.getTag() != null;
        stack.getTag().putLong("SellValue", (long) (((double) value) * ratio));
        return stack;
    }
}
