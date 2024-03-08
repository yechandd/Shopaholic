package uk.joshiejack.shopaholic.world.shop.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shop.Department;

import javax.annotation.Nonnull;
import java.util.Map;

public class Inventory extends SavedData {
    private final Map<Department, Stock> stock = new Object2ObjectOpenHashMap<>();

    public Inventory() {}
    public Inventory(CompoundTag nbt) {
        ListTag list = nbt.getList("Departments", 10);
        list.forEach(inbt -> {
            CompoundTag tag = (CompoundTag) inbt;
            Department department = Shopaholic.ShopaholicRegistries.DEPARTMENTS.get(new ResourceLocation(tag.getString("Department")));
            if (department != null)
                getStock(department).deserializeNBT(tag.getCompound("Stock"));
        });
    }

    public static Inventory get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(Inventory::new, Inventory::new), "stock_levels");
    }

    public static Stock getStock(ServerLevel world, Department department) {
        return get(world).getStock(department);
    }

    private Stock getStock(Department department) {
        return stock.computeIfAbsent(department, Stock::new);
    }

    public static void setChanged(ServerLevel world) {
        get(world).setDirty();
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        ListTag list = new ListTag();
        Shopaholic.ShopaholicRegistries.DEPARTMENTS.registry().values().forEach(department -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Department", department.id().toString());
            tag.put("Stock", getStock(department).serializeNBT());
            list.add(tag);
        });

        nbt.put("Departments", list);
        return nbt;
    }
}
