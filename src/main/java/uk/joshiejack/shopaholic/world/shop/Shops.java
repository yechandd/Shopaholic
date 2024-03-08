package uk.joshiejack.shopaholic.world.shop;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.util.helper.TagHelper;
import uk.joshiejack.shopaholic.Shopaholic;

import java.util.HashMap;
import java.util.Map;

public class Shops extends SavedData {
    public Map<ResourceLocation, CustomShop> customShops = new HashMap<>();
    public Map<ResourceLocation, CustomDepartment> customDepartments = new HashMap<>();

    public Shops() {
    }

    public Shops(CompoundTag tag) {
        if (tag.contains("Shops")) {
            Map<ResourceLocation, Shop> tempShops = new HashMap<>();
            TagHelper.readMap(tag.getList("Shops", 10),
                    tempShops,
                    (nbt) -> new ResourceLocation(nbt.getString("ID")),
                    (nbt) -> Shop.CODEC
                            .parse(NbtOps.INSTANCE, nbt.getCompound("Data"))
                            .resultOrPartial(Shopaholic.LOGGER::error)
                            .orElseThrow());
            tempShops.forEach((id, shop) -> customShops.put(id, new CustomShop(id, shop)));
        }

        if (tag.contains("Departments")) {
            Map<ResourceLocation, Department> tempDepartments = new HashMap<>();
            TagHelper.readMap(tag.getList("Departments", 10),
                    tempDepartments,
                    (nbt) -> new ResourceLocation(nbt.getString("ID")),
                    (nbt) -> Department.CODEC
                            .parse(NbtOps.INSTANCE, nbt.getCompound("Data"))
                            .resultOrPartial(Shopaholic.LOGGER::error)
                            .orElseThrow());
            tempDepartments.forEach((id, department) -> customDepartments.put(id, new CustomDepartment(id, department)));
        }
    }

    public static Shops get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(Shops::new, Shops::new), "shops");
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.put("Shops", TagHelper.writeMap(customShops, (nbt, key) -> {
            nbt.putString("ID", key.toString());
        }, (nbt, shop) -> Shop.CODEC
                .encodeStart(NbtOps.INSTANCE, shop)
                .resultOrPartial(Shopaholic.LOGGER::error)
                .ifPresent(data -> nbt.put("Data", data))));

        tag.put("Departments", TagHelper.writeMap(customDepartments, (nbt, key) -> {
            nbt.putString("ID", key.toString());
        }, (nbt, department) -> Department.CODEC
                .encodeStart(NbtOps.INSTANCE, department)
                .resultOrPartial(Shopaholic.LOGGER::error)
                .ifPresent(data -> nbt.put("Data", data))));
        return tag;
    }

    public static class CustomShop extends Shop {
        private final ResourceLocation id;

        public CustomShop(ResourceLocation id, Shop shop) {
            super(shop);
            this.id = id;
        }

        @Override
        public ResourceLocation id() {
            return id;
        }
    }

    public static class CustomDepartment extends Department {
        private final ResourceLocation id;

        public CustomDepartment(ResourceLocation id, Department department) {
            super(department);
            this.id = id;
        }

        @Override
        public ResourceLocation id() {
            return id;
        }
    }
}
