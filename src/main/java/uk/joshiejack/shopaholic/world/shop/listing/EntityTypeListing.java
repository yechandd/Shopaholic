package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.penguinlib.util.icon.EntityIcon;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.api.shop.ListingType;

public record EntityTypeListing(EntityType<?> entityType) implements ListingType {
    public static final Codec<EntityTypeListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("stack").forGetter(handler -> handler.entityType)
    ).apply(instance, EntityTypeListing::new));
    
    @Override
    public Icon createIcon() {
        return new EntityIcon(entityType.builtInRegistryHolder(), 1, 1);
    }

    @Override
    public void purchase(Player player) {
        if (!player.level().isClientSide)
            entityType.spawn((ServerLevel) player.level(), null, player, player.blockPosition(), MobSpawnType.SPAWN_EGG, true, true);
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return entityType.getDescription();
    }

    @Override
    public String id() {
        return RegistryHelper.id(entityType).toString();
    }

    @Override
    public boolean isValid() {
        return entityType != null;
    }
}
