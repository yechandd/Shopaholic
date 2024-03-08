package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shop.Shop;
import uk.joshiejack.shopaholic.world.shop.ShopLoader;

import java.util.Map;

public class ShopaholicShops extends AbstractPenguinRegistryProvider<Shop> {
    public static final ResourceLocation PIGGY_BANK_V_2 = Shopaholic.prefix("piggy_bank_v2");
    public ShopaholicShops(PackOutput output) {
        super(output, Shopaholic.ShopaholicRegistries.SHOPS);
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, Shop> map) {
        map.put(PIGGY_BANK_V_2, new Shop(Component.literal("Piggy Bank V2"), ShopLoader.DEFAULT_BACKGROUND, ShopLoader.EXTRA));
    }
}
