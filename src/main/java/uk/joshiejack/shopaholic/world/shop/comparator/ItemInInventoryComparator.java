package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.List;

public record ItemInInventoryComparator(List<Ingredient> ingredients) implements Comparator {
    public static final Codec<ItemInInventoryComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(inst -> inst.ingredients)
    ).apply(instance, ItemInInventoryComparator::new));


    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return PlayerHelper.getInventoryStream(target.getPlayer())
                .filter(stack -> ingredients.stream().anyMatch(ingredient -> ingredient.test(stack)))
                .mapToInt(ItemStack::getCount)
                .sum();
    }
}