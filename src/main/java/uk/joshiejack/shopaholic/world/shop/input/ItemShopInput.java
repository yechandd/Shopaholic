package uk.joshiejack.shopaholic.world.shop.input;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.shopaholic.api.shop.ShopInput;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public class ItemShopInput extends ShopInput<Item> {
    public static int ID = 2;

    private final Lazy<ItemStack> asStack = Lazy.of(() -> new ItemStack(id));

    @Override
    public int intID() {
        return ID;
    }

    public ItemShopInput(Item item) {
        super(BuiltInRegistries.ITEM, item);
    }

    public ItemShopInput(FriendlyByteBuf buf) {
        super(BuiltInRegistries.ITEM, buf);
    }

    @Override
    public String getName(ShopTarget target) {
        return asStack.get().getHoverName().getString();
    }
}