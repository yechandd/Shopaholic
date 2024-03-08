package uk.joshiejack.shopaholic.world.shipping;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.event.ItemGetValueEvent;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class ShippingRegistry {
    private static final Object2LongMap<Item> itemToValue = new Object2LongOpenHashMap<>();
    private static final Object2LongMap<ResourceLocation> tagToValue = new Object2LongOpenHashMap<>();

    @SuppressWarnings("ConstantConditions")
    public static long getValue(ItemStack stack) {
        long value = stack.hasTag() && stack.getTag().contains("SellValue") ? stack.getTag().getLong("SellValue") : itemToValue.getLong(stack.getItem());
        if (value == 0) {
            value = stack.getItemHolder().tags()
                    .filter(tag -> tagToValue.containsKey(tag.location()))
                    .mapToLong(tag -> tagToValue.getLong(tag.location()))
                    .findFirst()
                    .orElse(0);
        }

        ItemGetValueEvent event = new ItemGetValueEvent(stack, value);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewValue();
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        itemToValue.clear();
        tagToValue.clear();
        event.table("item_values").rows().stream()
                .filter(row -> row.item() != null && !row.isEmpty("days"))
                .forEach(row -> itemToValue.put(row.item(), row.getAsLong("days")));
        event.table("tag_values").rows().stream()
                .filter(row -> row.getRL("tag") != null && !row.isEmpty("days"))
                .forEach(row -> tagToValue.put(row.getRL("tag"), row.getAsLong("days")));
    }
}
