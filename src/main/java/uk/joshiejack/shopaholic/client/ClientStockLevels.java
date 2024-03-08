package uk.joshiejack.shopaholic.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.inventory.Stock;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientStockLevels {
    private static final Map<Department, Stock> stock = new Object2ObjectOpenHashMap<>();

    public static Stock getStock(Department department) {
        return stock.computeIfAbsent(department, Stock::new);
    }
}