package uk.joshiejack.shopaholic.world.shop.input;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.FriendlyByteBuf;
import uk.joshiejack.shopaholic.api.shop.ShopInput;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public class EntityShopInput extends ShopInput<EntityType<?>> {
    public static int ID = 1;

    public EntityShopInput(Entity entity) {
        super(BuiltInRegistries.ENTITY_TYPE, entity.getType());
    }

    public EntityShopInput(EntityType<?> type) {
        super(BuiltInRegistries.ENTITY_TYPE, type);
    }

    public EntityShopInput(FriendlyByteBuf buf) {
        super(BuiltInRegistries.ENTITY_TYPE, buf);
    }

    @Override
    public String getName(ShopTarget target) {
        return target.getEntity().getName().getString();
    }

    @Override
    public int intID() {
        return ID;
    }
}
