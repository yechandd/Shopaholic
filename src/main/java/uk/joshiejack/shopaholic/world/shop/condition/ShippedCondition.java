package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.client.shipping.Shipped;
import uk.joshiejack.shopaholic.world.shipping.Market;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ShippedCondition(List<Ingredient> ingredient, int required) implements Condition {
    public static final Codec<ShippedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(condition -> condition.ingredient),
            Codec.INT.fieldOf("required").forGetter(condition -> condition.required)

    ).apply(instance, ShippedCondition::new));

    @SuppressWarnings("unused")
    public static Builder hasShipped(int requiredAmount) {
        return new Builder(requiredAmount);
    }

    private Set<Shipping.SoldItem> getHolderSet(Level world, Player player) {
        return world.isClientSide ? Shipped.getSold() : Market.get((ServerLevel) world).getShippingForPlayer(player).getSold();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        int total = 0;
        Set<Shipping.SoldItem> sold = getHolderSet(target.getLevel(), target.getPlayer());
        for (Shipping.SoldItem holder : sold) {
            if (ingredient.stream().anyMatch(i -> i.test(holder.getStack()))) {
                total += holder.getStack().getCount();
                //Return early if this is true, no need to keep counting............
                if (total >= required) {
                    return true;
                }
            }
        }

        return false;
    }

    //Conditions
    public static class Builder {
        private final List<Ingredient> items = new ArrayList<>();
        private final int requiredAmount;

        public Builder(int requiredAmount) {
            this.requiredAmount = requiredAmount;
        }

        public Builder requireItem(Item item) {
            items.add(Ingredient.of(item));
            return this;
        }

        public Builder requireTag(TagKey<Item> tag) {
            items.add(Ingredient.of(tag));
            return this;
        }

        public Condition build() {
            return new ShippedCondition(items , requiredAmount);
        }
    }
}
