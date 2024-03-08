package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record InDimensionCondition(ResourceKey<Level> dimension) implements Condition {
    public static final Codec<InDimensionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(condition -> condition.dimension)
    ).apply(instance, InDimensionCondition::new));

    @SuppressWarnings("unused")
    public static Condition inDimension(ResourceKey<Level> world) {
        return new InDimensionCondition(world);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return target.getLevel().dimension().equals(dimension);
    }
}
