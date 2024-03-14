package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.Locale;

public record EntityHasNBTTagCondition(TargetType targetType, FieldType dataType, String key, String value) implements Condition {
    public static final Codec<EntityHasNBTTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(TargetType::values).fieldOf("target").forGetter(condition -> condition.targetType),
            StringRepresentable.fromEnum(FieldType::values).fieldOf("data_type").forGetter(condition -> condition.dataType),
            Codec.STRING.fieldOf("key").forGetter(condition -> condition.key),
            Codec.STRING.fieldOf("value").forGetter(condition -> condition.value)
    ).apply(instance, EntityHasNBTTagCondition::new));

    public enum FieldType implements StringRepresentable {
        BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, INT_ARRAY, LONG_ARRAY;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public static Condition entityHasNBTTag(String key, String value) {
        return new EntityHasNBTTagCondition(TargetType.ENTITY, FieldType.STRING, key, value);
    }

    public static Condition playerHasNBTTag(String key, String value) {
        return new EntityHasNBTTagCondition(TargetType.PLAYER, FieldType.STRING, key, value);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        if (type == CheckType.SHOP_LISTING) return false;
        CompoundTag data = targetType == TargetType.ENTITY ? target.getEntity().saveWithoutId(new CompoundTag()) : target.getPlayer().saveWithoutId(new CompoundTag());
        return HasNBTTagConditionHelper.matches(data, dataType, key, value);
    }

    public enum TargetType implements StringRepresentable {
        PLAYER, ENTITY;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}