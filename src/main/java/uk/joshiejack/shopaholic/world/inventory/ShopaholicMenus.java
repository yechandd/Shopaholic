package uk.joshiejack.shopaholic.world.inventory;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.inventory.DepartmentMenu;
import uk.joshiejack.shopaholic.world.inventory.EconomyManagerMenu;

public class ShopaholicMenus {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Shopaholic.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<EconomyManagerMenu>> BOOK = CONTAINERS.register("economy_manager", () -> IMenuTypeExtension.create((id, inv, data) -> new EconomyManagerMenu(id)));
    public static final DeferredHolder<MenuType<?>, MenuType<DepartmentMenu>> SHOP = CONTAINERS.register("shop", () -> IMenuTypeExtension.create((id, inv, data) -> new DepartmentMenu(id, inv.player, data)));
}
