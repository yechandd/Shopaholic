package uk.joshiejack.shopaholic.world.shipping;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Market extends SavedData {
    private final Map<UUID, Shipping> shippingData = new HashMap<>();
    
    public static Market get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(Market::new, Market::new), "market");
    }

    public Market() {}
    public Market(CompoundTag nbt) {
        nbt.getList("Shipping", 10).stream().map(e -> (CompoundTag)e).forEach(tag -> {
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            shippingData.put(uuid, new Shipping(this, uuid));
            shippingData.get(uuid).deserializeNBT(tag.getCompound("Data"));
        });
    }

    public void newDay(ServerLevel world) {
        shippingData.values().forEach(s -> s.onNewDay(world));
    }

    private Shipping getShippingForTeam(UUID uuid) {
        if (!shippingData.containsKey(uuid)) {
            Shipping shipping = new Shipping(this, uuid);
            shippingData.put(uuid, shipping);
            setDirty();
            return shipping;
        } else return shippingData.get(uuid);
    }

    public Shipping getShippingForPlayer(Player player) {
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        boolean shared = player.getPersistentData().contains("ShopaholicSettings") &&
                player.getPersistentData().getCompound("ShopaholicSettings").getBoolean("SharedWallet"); //Player's Shared gold status
        return shared ? getShippingForTeam(team.getID()).shared() : getShippingForTeam(player.getUUID());
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        ListTag list = new ListTag();
        shippingData.forEach((uuid, vault) -> {
            CompoundTag data = new CompoundTag();
            data.putString("UUID", uuid.toString());
            data.put("Data", vault.serializeNBT());
            list.add(data);
        });

        nbt.put("Shipping", list);
        return nbt;
    }
}
