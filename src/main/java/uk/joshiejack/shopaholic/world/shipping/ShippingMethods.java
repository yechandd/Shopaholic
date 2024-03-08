package uk.joshiejack.shopaholic.world.shipping;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.shopaholic.Shopaholic;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class ShippingMethods {
    public static final TagKey<Block> SHIPPING_BIN_BLOCK = BlockTags.create(new ResourceLocation(Shopaholic.MODID, "shipping_bin"));
    public static final TagKey<Item> SHIPPING_BIN_ITEM = ItemTags.create(new ResourceLocation(Shopaholic.MODID, "shipping_bin"));
    public static final TagKey<EntityType<?>> SHIPPING_BIN_ENTITY = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Shopaholic.MODID, "shipping_bin"));

    private static boolean ship(Player player, ItemStack stack) {
        Level world = player.level();
        long value = ShippingRegistry.getValue(stack);
        if (value > 0) {
            if (!world.isClientSide) {
                int count = player.isShiftKeyDown() ? stack.getCount() : 1;
                ItemStack inserted = stack.copy();
                inserted.setCount(count);
                Market.get((ServerLevel) world).getShippingForPlayer(player).add(inserted);
                stack.shrink(count);
            }

            return true;
        }

        return false;
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().getBlockState(event.getPos()).is(SHIPPING_BIN_BLOCK)
                && ship(event.getEntity(), event.getItemStack()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().getType().is(SHIPPING_BIN_ENTITY)
                && ship(event.getEntity(), event.getItemStack()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(SHIPPING_BIN_ITEM)) {
            MutableBoolean cancel = new MutableBoolean(false);
            PlayerHelper.getInventoryStream(event.getEntity())
                    .filter(item -> !item.isEmpty() && item != event.getItemStack())
                    .forEach(item -> {
                        if (ship(event.getEntity(), item))
                            cancel.setTrue();
                    });
            if (cancel.isTrue())
                event.setCanceled(true);
        }
    }
}
