package uk.joshiejack.shopaholic.data.shop.comparator;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemComparatorBuilder implements ComparatorBuilder {
    protected final List<Ingredient> ingredients = new ArrayList<>();


    @SuppressWarnings("unused")
    public AbstractItemComparatorBuilder countItem(Item item) {
        ingredients.add(Ingredient.of(item));
        return this;
    }

    @SuppressWarnings("unused")
    public AbstractItemComparatorBuilder countTag(TagKey<Item> tag) {
        ingredients.add(Ingredient.of(tag));
        return this;
    }
}