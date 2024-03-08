package uk.joshiejack.shopaholic.world.bank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.network.bank.SetActiveWalletPacket;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Bank extends SavedData {
    private final Map<UUID, Vault> vaults = new HashMap<>();

    public Bank() {}
    public Bank(CompoundTag nbt) {
        nbt.getList("Vaults", 10).stream().map(e -> (CompoundTag)e).forEach(tag -> {
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            vaults.put(uuid, new Vault(this, uuid));
            vaults.get(uuid).deserializeNBT(tag.getCompound("Data"));
        });
    }

    public static Bank get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(Bank::new, Bank::new), "bank");
    }

    public Vault getVaultForTeam(UUID uuid) {
        if (!vaults.containsKey(uuid)) {
            Vault vault = new Vault(this, uuid);
            vaults.put(uuid, vault);
            setDirty();
            return vault;
        } else return vaults.get(uuid);
    }

    public Vault getVaultForPlayer(Player player) {
        //Change this to be based on a person player toggle instead
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        boolean shared = player.getPersistentData().contains("ShopaholicSettings") &&
                player.getPersistentData().getCompound("ShopaholicSettings").getBoolean("SharedWallet"); //Player's Shared gold status
        return shared ? getVaultForTeam(team.getID()).shared() : getVaultForTeam(player.getUUID()).personal();
    }

    public void syncToPlayer(ServerPlayer player) {
        //Sync both
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        boolean shared = player.getPersistentData().contains("ShopaholicSettings") &&
                player.getPersistentData().getCompound("ShopaholicSettings").getBoolean("SharedWallet"); //Player's Shared gold status
        PenguinNetwork.sendToClient(player, new SetActiveWalletPacket(shared));
        getVaultForTeam(team.getID()).shared().synchronize((ServerLevel) player.level());
        getVaultForTeam(player.getUUID()).personal().synchronize((ServerLevel) player.level());
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        ListTag list = new ListTag();
        vaults.forEach((uuid, vault) -> {
            CompoundTag data = new CompoundTag();
            data.putString("UUID", uuid.toString());
            data.put("Data", vault.serializeNBT());
            list.add(data);
        });

        nbt.put("Vaults", list);
        return nbt;
    }
}
