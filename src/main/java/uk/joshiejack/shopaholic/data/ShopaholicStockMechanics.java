package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shop.inventory.StockMechanic;

import java.util.Map;

public class ShopaholicStockMechanics extends AbstractPenguinRegistryProvider<StockMechanic> {
    public static final ResourceLocation ONLY_ONE = new ResourceLocation(Shopaholic.MODID, "only_one");
    public static final ResourceLocation LIMITED_ONE = new ResourceLocation(Shopaholic.MODID, "limited_one");

    public ShopaholicStockMechanics(PackOutput output) {
        super(output, Shopaholic.ShopaholicRegistries.STOCK_MECHANICS);
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, StockMechanic> map) {
        map.put(StockMechanic.UNLIMITED_ID, StockMechanic.UNLIMITED);
        map.put(ONLY_ONE, new StockMechanic(1, 0));
        map.put(LIMITED_ONE, new StockMechanic(1, 1));
    }
}
