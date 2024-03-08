package uk.joshiejack.shopaholic.network.bank;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.client.bank.Wallet;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncGoldPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_gold");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private final WalletType type;
    private final long balance;
    private final long income;
    private final long expenses;

    @SuppressWarnings("unused")
    public SyncGoldPacket(WalletType type, long balance, long income, long expenses) {
        this.type = type;
        this.balance = balance;
        this.income = income;
        this.expenses = expenses;
    }

    public SyncGoldPacket(FriendlyByteBuf from) {
        type = from.readBoolean() ? WalletType.SHARED : WalletType.PERSONAL;
        balance = from.readLong();
        income = from.readLong();
        expenses = from.readLong();
    }

    @Override
    public void write(FriendlyByteBuf to) {        to.writeBoolean(type == WalletType.SHARED);
        to.writeLong(balance);
        to.writeLong(income);
        to.writeLong(expenses);
    }



    @Override
    public void handle(Player player) {
        Wallet.setGold(type, balance, income, expenses);
    }
}
