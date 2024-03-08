package uk.joshiejack.shopaholic.world.shop.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.MathHelper;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.network.shop.SetStockedItemPacket;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

import java.util.Map;

public class Stock implements INBTSerializable<CompoundTag> {
    private static final RandomSource initialRandom = RandomSource.create();
    private final Object2IntMap<Listing> stockLevels = new Object2IntOpenHashMap<>();
    private final Map<Listing, String> stockItems = new Object2ObjectOpenHashMap<>();
    private final Department department;

    public Stock(Department department) {
        this.department = department;
        for (Listing listing : department.getListings())
            if (!listing.isSingleEntry() && !stockItems.containsKey(listing))
                stockItems.put(listing, listing.getRandomID(initialRandom));
    }

    public void decreaseStockLevel(Listing listing) {
        stockLevels.computeIfAbsent(listing, (l) -> listing.getStockMechanic().maximum()); //Ensure we have a stock level for this item
        stockLevels.mergeInt(listing, -1, Integer::sum);
    }

    public void setStockedItem(Listing listing, String stockID) {
        stockItems.put(listing, stockID);
    }

    @OnlyIn(Dist.CLIENT)
    public void setStockLevel(Listing listing, int stock) {
        stockLevels.put(listing, stock);
        if (stock <= 0)
            ShopaholicClient.refreshShop(); //Stock levels have changed
    }

    public String getStockedObject(Listing listing) {
        return stockItems.computeIfAbsent(listing, (l) -> listing.getRandomID(initialRandom));
    }

    public int getStockLevel(Listing listing) {
        return stockLevels.getOrDefault(listing, listing.getStockMechanic().maximum());
    }

    public void newDay(RandomSource random) {
        for (Listing listing : stockLevels.keySet()) {
            StockMechanic mechanic = listing.getStockMechanic();
            stockLevels.put(listing, MathHelper.constrainToRangeInt(getStockLevel(listing) + mechanic.increase(), 0, mechanic.maximum()));
        }

        for (Listing listing : stockItems.keySet()) {
            String id = listing.getRandomID(random);
            stockItems.put(listing, id);
            PenguinNetwork.sendToEveryone(new SetStockedItemPacket(department, listing, id));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag data = new CompoundTag();
        ListTag list = new ListTag();
        stockLevels.forEach((key, value) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Key", key.id());
            tag.putInt("Value", value);
            list.add(tag);
        });

        data.put("Levels", list);

        ListTag stockList = new ListTag();
        stockItems.forEach((key, value) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Key", key.id());
            tag.putString("Value", value);
            stockList.add(tag);
        });

        data.put("Stock", stockList);

        return data;
    }

    @Override
    public void deserializeNBT(CompoundTag data) {
        stockLevels.clear(); // using the same instance
        stockItems.clear(); // so make sure to clear this each time
        ListTag nbt = data.getList("Levels", 10);
        for (int i = 0; i < nbt.size(); i++) {
            CompoundTag tag = nbt.getCompound(i);
            Listing listing = department.getListingByID(tag.getString("Key"));
            if (listing != null)
                stockLevels.put(listing, tag.getInt("Value"));
        }

        ListTag stock = data.getList("Stock", 10);
        for (int i = 0; i < stock.size(); i++) {
            CompoundTag tag = stock.getCompound(i);
            Listing listing = department.getListingByID(tag.getString("Key"));
            if (listing != null)
                stockItems.put(listing, tag.getString("Value"));
        }
    }
}
