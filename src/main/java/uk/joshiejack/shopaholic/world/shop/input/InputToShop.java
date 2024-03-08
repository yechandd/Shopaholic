package uk.joshiejack.shopaholic.world.shop.input;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.world.shop.Department;

import javax.annotation.Nullable;
import java.util.Collection;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class InputToShop {
    public static final Multimap<BlockShopInput, Department> BLOCK_TO_SHOP = HashMultimap.create();
    public static final Multimap<EntityShopInput, Department> ENTITY_TO_SHOP = HashMultimap.create();
    public static final Multimap<ItemShopInput, Department> ITEM_TO_SHOP = HashMultimap.create();
    public static final Multimap<String, Department> COMMAND_TO_SHOP = HashMultimap.create();

    public static void register(String type, String data, Department department) {
        switch (type) {
            case "block":
                BLOCK_TO_SHOP.get(new BlockShopInput(BuiltInRegistries.BLOCK.get(new ResourceLocation(data)))).add(department);
                break;
            case "entity":
                ENTITY_TO_SHOP.get(new EntityShopInput(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(data)))).add(department);
                break;
            case "item":
                ITEM_TO_SHOP.get(new ItemShopInput(BuiltInRegistries.ITEM.get(new ResourceLocation(data)))).add(department);
                break;
            case "command":
                COMMAND_TO_SHOP.get(data).add(department);
                break;
        }
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        InputToShop.BLOCK_TO_SHOP.clear(); //Clear the block - > shop mappings
        InputToShop.ENTITY_TO_SHOP.clear(); //Clear the entity - > shop mappings
        InputToShop.ITEM_TO_SHOP.clear(); //Clear the item - > shop mappings
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        BlockShopInput input = new BlockShopInput(event.getLevel().getBlockState(event.getPos()).getBlock());
        if(open(BLOCK_TO_SHOP.get(input),
                new ShopTarget(event.getLevel(), event.getPos(), event.getEntity(), event.getEntity(), event.getItemStack(), input),
                event.getEntity().isShiftKeyDown() ? InputMethod.SHIFT_RIGHT_CLICK : InputMethod.RIGHT_CLICK))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        EntityShopInput input = new EntityShopInput(event.getTarget());
        if(open(ENTITY_TO_SHOP.get(input),
                new ShopTarget(event.getLevel(), event.getPos(), event.getTarget(), event.getEntity(), event.getItemStack(), input),
                event.getEntity().isShiftKeyDown() ? InputMethod.SHIFT_RIGHT_CLICK : InputMethod.RIGHT_CLICK))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide) return;
        ItemShopInput input = new ItemShopInput(event.getItemStack().getItem());
        if(open(ITEM_TO_SHOP.get(input),
                new ShopTarget(event.getLevel(), event.getPos(), event.getEntity(), event.getEntity(), event.getItemStack(), input),
                event.getEntity().isShiftKeyDown() ? InputMethod.SHIFT_RIGHT_CLICK : InputMethod.RIGHT_CLICK))
            event.setCanceled(true);
    }

    public static boolean open(Collection<Department> shops, ShopTarget target, InputMethod method) {
        Department shop = getFirstShop(shops, target, Condition.CheckType.SHOP_IS_OPEN, method);
        if (shop != null) {
            shop.open(target, true);
            return true;
        }

        return false;
    }

    @Nullable
    public static Department getFirstShop(Collection<Department> shops, ShopTarget target, Condition.CheckType type, InputMethod method) {
        for (Department shop: shops) {
            if (shop.isValidFor(target, type, method)) {
                return shop;
            }
        }

        return null;
    }
}
