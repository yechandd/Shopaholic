package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

public record ItemInHandComparator(List<Ingredient> ingredients) implements Comparator {
    public static final Codec<ItemInHandComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(inst -> inst.ingredients)
    ).apply(instance, ItemInHandComparator::new));


    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return Stream.of(target.getPlayer().getMainHandItem(), target.getPlayer().getOffhandItem())
                .filter(stack -> ingredients.stream().anyMatch(ingredient -> ingredient.test(stack)))
                .mapToInt(ItemStack::getCount)
                .sum();
    }
}