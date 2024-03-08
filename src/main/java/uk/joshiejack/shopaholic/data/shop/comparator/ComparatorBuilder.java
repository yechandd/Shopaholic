package uk.joshiejack.shopaholic.data.shop.comparator;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.*;

//TODO ALL THE COMPARATORS
public interface ComparatorBuilder {

    @SuppressWarnings("unused")
    static AddComparatorBuilder add(ComparatorBuilder... comparators) {
        return new AddComparatorBuilder(comparators);
    }

    @SuppressWarnings("unused")
    static BlockTagOnTargetComparatorBuilder blockTag(TagKey<Block> tag) {
        return new BlockTagOnTargetComparatorBuilder(tag);
    }

    @SuppressWarnings("unused")
    static ComparatorBuilder canSeeSky() {
        return () -> CanSeeSkyComparator.INSTANCE;
    }


    @SuppressWarnings("unused")
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
    public static ComparatorBuilder lightLevel() {
        return () -> LightLevelComparator.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static NumberComparatorBuilder number(int value) {
        return new NumberComparatorBuilder(value);
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder playerHealth() { return () -> PlayerHealthComparator.INSTANCE; }

    @SuppressWarnings("unused")
    public static StatusComparatorBuilder playerStatus(String status) {
        return new StatusComparatorBuilder("player", status);
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder playerX() {
        return () -> PlayerPositionComparator.X.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder playerY() {
        return () -> PlayerPositionComparator.Y.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder playerZ() {
        return () -> PlayerPositionComparator.Z.INSTANCE;
    }


    @SuppressWarnings("unused")
    public static ComparatorBuilder rainLevel() {
        return () -> RainLevelComparator.INSTANCE;
    }


    @SuppressWarnings("unused")
    public static ComparatorBuilder redstoneSignal() {
        return () -> RedstoneLevelComparator.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder temperature() { return () -> TemperatureComparator.INSTANCE; }

    @SuppressWarnings("unused")
    public static AbstractItemComparatorBuilder shippedAmount() {
        return new AbstractItemComparatorBuilder() {
            @Override
            public Comparator build() {
                return new ShippedCountComparator(ingredients);
            }
        };
    }

    @SuppressWarnings("unused")
    public static StatusComparatorBuilder teamStatus(String status) {
        return new StatusComparatorBuilder("team", status);
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder vendorHealth() { return () -> VendorHealthComparator.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder vendorX() {
        return () -> VendorPositionComparator.X.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder vendorY() {
        return () -> VendorPositionComparator.Y.INSTANCE;
    }

    @SuppressWarnings("unused")
    public static ComparatorBuilder vendorZ() {
        return () -> VendorPositionComparator.Z.INSTANCE;
    }

    Comparator build();
}
