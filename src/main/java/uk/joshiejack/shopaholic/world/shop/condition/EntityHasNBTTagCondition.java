package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.Locale;

public record EntityHasNBTTagCondition(TargetType targetType, NbtPredicate nbtPredicate) implements Condition {
    public static final Codec<EntityHasNBTTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(TargetType::values).fieldOf("target").forGetter(condition -> condition.targetType),
            NbtPredicate.CODEC.fieldOf("nbt").forGetter(condition -> condition.nbtPredicate)
    ).apply(instance, EntityHasNBTTagCondition::new));

    public static Condition entityHasNBTTag(CompoundTag tagData) {
        return new EntityHasNBTTagCondition(TargetType.ENTITY, new NbtPredicate(tagData));
    }

    public static Condition playerHasNBTTag(CompoundTag tagData) {
        return new EntityHasNBTTagCondition(TargetType.PLAYER, new NbtPredicate(tagData));
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return targetType == TargetType.PLAYER ? nbtPredicate.matches(target.getPlayer()) : nbtPredicate.matches(target.getEntity());
    }

    public enum TargetType implements StringRepresentable {
        PLAYER, ENTITY;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}