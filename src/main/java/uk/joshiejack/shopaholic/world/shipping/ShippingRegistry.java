package uk.joshiejack.shopaholic.world.shipping;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.event.ItemGetValueEvent;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShippingRegistry {
    public static final DataMapType<Item, Long> SELL_VALUE = DataMapType.builder(new ResourceLocation(Shopaholic.MODID, "sell_value"),
            Registries.ITEM, Codec.LONG).synced(Codec.LONG, true).build();

    @SuppressWarnings("ConstantConditions")
    public static long getValue(ItemStack stack) {
        Long valueObj = stack.hasTag() && stack.getTag().contains("SellValue") ? stack.getTag().getLong("SellValue") :
                stack.getItemHolder().getData(SELL_VALUE);
        long value = valueObj != null ? valueObj : 0;
        ItemGetValueEvent event = new ItemGetValueEvent(stack, value);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewValue();
    }

    @SubscribeEvent
    public static void onDataMap(RegisterDataMapTypesEvent event) {
        event.register(SELL_VALUE);
    }
}