package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class HasPetCondition implements Condition {
    public static final Codec<HasPetCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(condition -> condition.type)
    ).apply(instance, HasPetCondition::new));
    private final Predicate<TamableAnimal> predicate;
    private final EntityType<?> type;

    public HasPetCondition(EntityType<?> type) {
        this.type = type;
        this.predicate = (e) -> e.getType() == type;
    }

    @SuppressWarnings("unused")
    public static Condition hasPet(EntityType<?> entityType) {
        return new HasPetCondition(entityType);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return target.getLevel().getEntitiesOfClass(TamableAnimal.class, target.getPlayer().getBoundingBox().inflate(128D), predicate).stream()
                .anyMatch(e -> target.getPlayer().getUUID().equals(e.getOwnerUUID()));
    }
}