package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public class EntityNearbyCondition implements Condition {
    public static final Codec<EntityNearbyCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(condition -> condition.type),
            Codec.DOUBLE.fieldOf("range").forGetter(condition -> condition.range)
    ).apply(instance, EntityNearbyCondition::new));

    private TargetingConditions predicate;
    private EntityType<?> type;
    private double range;

    public EntityNearbyCondition() {}
    public EntityNearbyCondition(EntityType<?> type, double range) {
        this.type = type;
        this.range = range;
        this.predicate = TargetingConditions.forNonCombat().range(range).selector((e) -> e.getType() == type);
    }

    @SuppressWarnings("unused")
    public static Condition entityNearby(EntityType<?> entityType, double range) {
        return new EntityNearbyCondition(entityType, range);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return !target.getLevel().getNearbyEntities(LivingEntity.class, predicate, target.getPlayer(), target.getPlayer().getBoundingBox().inflate(range)).isEmpty();
    }
}