package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ItemStackHasNBTTagCondition extends AbstractHasNBTTagCondition<ItemStack> {
    public static final Codec<ItemStackHasNBTTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("tag").forGetter(condition -> condition.tag)
    ).apply(instance, ItemStackHasNBTTagCondition::new));

    public ItemStackHasNBTTagCondition(CompoundTag tag) {
        this.tag = tag;
        this.predicate = createPredicate(tag);
    }

    public static Condition itemHasNBTTag(CompoundTag tagData) {
        return new ItemStackHasNBTTagCondition(tagData);
    }

    @Override
    protected Predicate<ItemStack> createPredicate(CompoundTag CompoundTag) {
        return (stack) -> NbtUtils.compareNbt(CompoundTag, stack.getTag(), true);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return target.getStack().hasTag() && predicate.test(target.getStack());
    }
}