package uk.joshiejack.shopaholic.data.shop.comparator;

import net.minecraft.world.item.Item;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.ItemInHandComparator;
import uk.joshiejack.shopaholic.world.shop.comparator.ItemInInventoryComparator;
import uk.joshiejack.shopaholic.world.shop.comparator.ShippedCountComparator;

//TODO ALL THE COMPARATORS
public interface ComparatorBuilder {

    public static AbstractItemComparatorBuilder held(String id) {
        return new AbstractItemComparatorBuilder() {
            @Override
            public Comparator build() {
                return new ItemInHandComparator(ingredients);
            }
        };
    }

    @SuppressWarnings("unused")
    static AbstractItemComparatorBuilder itemInInventory(String id, Item item) {
        return new AbstractItemComparatorBuilder() {
            @Override
            public Comparator build() {
                return new ItemInInventoryComparator(ingredients);
            }
        };
    }

    @SuppressWarnings("unused")
    static KubeJSComparatorBuilder kubejs() {
        return new KubeJSComparatorBuilder();
    }

    @SuppressWarnings("unused")
    public static AbstractItemComparatorBuilder shippedAmount() {
        return new AbstractItemComparatorBuilder() {
            @Override
            public Comparator build() {
                return new ShippedCountComparator(ingredients);
            }
        };
    }

    Comparator build();
}
