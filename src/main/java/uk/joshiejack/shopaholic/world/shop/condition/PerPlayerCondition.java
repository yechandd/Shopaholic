package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

import javax.annotation.Nonnull;

import static uk.joshiejack.shopaholic.Shopaholic.MODID;

public record PerPlayerCondition(int max) implements Condition {
    public static final Codec<PerPlayerCondition> CODEC = Codec.INT.fieldOf("max").xmap(PerPlayerCondition::new, condition -> condition.max).codec();

    @SuppressWarnings("unused")
    public static Condition perPlayer(int max) {
        return new PerPlayerCondition(max);
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return type == CheckType.SHOP_LISTING;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull Department department, @Nonnull Listing listing, @Nonnull CheckType type) {
        if (type != CheckType.SHOP_LISTING) return false;
        else {
            CompoundTag tag = target.getPlayer().getPersistentData().getCompound(MODID);
            String label = department.id() + ":" + listing.id();
            return tag.getInt(label) < max;
        }
    }

    @Override
    public void onPurchase(Player player, @Nonnull Department department, @Nonnull Listing listing) {
        CompoundTag tag = player.getPersistentData().getCompound(MODID);
        String label = department.id() + ":" + listing.id();
        tag.putInt(label, tag.getInt(label) + 1);
        player.getPersistentData().put(MODID, tag);
        if (player.level().isClientSide && tag.getInt(label) >= max) {
            ShopaholicClient.refreshShop();
        }
    }
}
