package uk.joshiejack.shopaholic.world.bank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.MathHelper;
import uk.joshiejack.shopaholic.ShopaholicServerConfig;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.network.bank.SyncGoldPacket;

import java.util.UUID;

public class Vault implements INBTSerializable<CompoundTag> {
    private final Bank bank;
    private final UUID uuid;
    private long balance = 0;
    private long expenses = 0;
    private long income = 0;
    private boolean shared;

    public Vault(Bank bank, UUID uuid) {
        this.bank = bank;
        this.uuid = uuid;
    }

    public Vault personal() {
        this.shared = false;
        return this;
    }

    public Vault shared() {
        this.shared = true;
        return this;
    }

    public long getBalance() {
        return balance;
    }

    public void decreaseBalance(Level world, long amount) {
        if (amount < 0) amount = 0;
        expenses += amount;
        setBalance(world, balance - amount);
    }

    public void increaseBalance(Level world, long amount) {
        if (amount < 0) amount = 0;
        income += amount;
        setBalance(world, balance + amount);
    }

    public void setBalance(Level world, long amount) {
        balance = MathHelper.constrainToRangeLong(amount, ShopaholicServerConfig.minGold.get(), ShopaholicServerConfig.maxGold.get());
        bank.setDirty(); //Mark for saving
        if (world instanceof ServerLevel)
            synchronize((ServerLevel) world); //Sync the data to the players
    }

    public void synchronize(ServerLevel world) {
        if (shared) {
            PenguinNetwork.sendToTeam(world, uuid, new SyncGoldPacket(WalletType.SHARED, balance, income, expenses));
        } else PenguinNetwork.sendToClient((ServerPlayer) world.getPlayerByUUID(uuid), new SyncGoldPacket(WalletType.PERSONAL, balance, income, expenses));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Balance", balance);
        tag.putLong("Expenses", expenses);
        tag.putLong("Income", income);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        balance = nbt.getLong("Balance");
        expenses = nbt.getLong("Expenses");
        income = nbt.getLong("Income");
    }
}
