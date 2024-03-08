package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.client.shipping.Shipped;
import uk.joshiejack.shopaholic.world.shipping.Market;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public record ShippedCountComparator(List<Ingredient> ingredients) implements Comparator {
    public static final Codec<ShippedCountComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredient").forGetter(inst -> inst.ingredients)
    ).apply(instance, ShippedCountComparator::new));


    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    private Set<Shipping.SoldItem> getSoldSet(Level world, Player player) {
        return world.isClientSide ? Shipped.getSold() : Market.get((ServerLevel) world).getShippingForPlayer(player).getSold();
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        int total = 0;
        Set<Shipping.SoldItem> sold = getSoldSet(target.getLevel(), target.getPlayer());
        for (Shipping.SoldItem holder : sold) {
            if (ingredients.stream().anyMatch(ingredient -> ingredient.test(holder.getStack()))) {
                total += holder.getStack().getCount();
            }
        }

        return total;
    }
}