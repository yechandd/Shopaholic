package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class BlockEntityHasNBTTagCondition extends AbstractHasNBTTagCondition<BlockEntity> {
    public static final Codec<BlockEntityHasNBTTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("tag").forGetter(condition -> condition.tag)
    ).apply(instance, BlockEntityHasNBTTagCondition::new));

    public BlockEntityHasNBTTagCondition(CompoundTag tag) {
        this.tag = tag;
        this.predicate = createPredicate(tag);
    }

    @SuppressWarnings("unused")
    public static Condition blockEntityHasNBTTag(CompoundTag tagData) {
        return new BlockEntityHasNBTTagCondition(tagData);
    }

    @Override
    protected Predicate<BlockEntity> createPredicate(CompoundTag tag) {
        return (tile) -> NbtUtils.compareNbt(tag, tile.serializeNBT(), true);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        BlockEntity tile = target.getLevel().getBlockEntity(target.getPos());
        return tile != null && predicate.test(tile);
    }
}