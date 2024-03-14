package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record BlockEntityHasNBTTagCondition(EntityHasNBTTagCondition.FieldType dataType, String key, String value) implements Condition {
    public static final Codec<BlockEntityHasNBTTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(EntityHasNBTTagCondition.FieldType::values).fieldOf("data_type").forGetter(condition -> condition.dataType),
            Codec.STRING.fieldOf("key").forGetter(condition -> condition.key),
            Codec.STRING.fieldOf("value").forGetter(condition -> condition.value)
    ).apply(instance, BlockEntityHasNBTTagCondition::new));

    @SuppressWarnings("unused")
    public static Condition blockEntityHasNBTTag(String key, String value) {
        return new BlockEntityHasNBTTagCondition(EntityHasNBTTagCondition.FieldType.STRING, key, value);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        BlockEntity tile = target.getLevel().getBlockEntity(target.getPos());
        return tile != null && HasNBTTagConditionHelper.matches(tile.saveWithFullMetadata(), dataType, key, value);
    }
}