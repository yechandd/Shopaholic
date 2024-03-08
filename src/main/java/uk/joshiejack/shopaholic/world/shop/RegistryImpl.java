package uk.joshiejack.shopaholic.world.shop;

import uk.joshiejack.shopaholic.api.ShopaholicAPI;
import uk.joshiejack.shopaholic.api.shop.*;

public class RegistryImpl implements ShopaholicAPI.IRegistry {
    @Override
    public void registerCostFormula(String name, CostFormula formula) {
        ShopRegistries.COST_FORMULAE.put(name, formula);
    }
}
