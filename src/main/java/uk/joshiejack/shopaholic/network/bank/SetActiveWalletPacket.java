package uk.joshiejack.shopaholic.network.bank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.network.AbstractSetPlayerNBTPacket;

@Packet(PacketFlow.CLIENTBOUND)
public class SetActiveWalletPacket extends AbstractSetPlayerNBTPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_active_wallet");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private final boolean shared;

    public SetActiveWalletPacket(boolean shared) {
        super("ShopaholicSettings");
        this.shared = shared;
    }

    public SetActiveWalletPacket (FriendlyByteBuf pb) {
        super("ShopaholicSettings");
        shared = pb.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pb) {
        pb.writeBoolean(shared);
    }


    @Override
    public void setData(CompoundTag tag) {
        tag.putBoolean("SharedWallet", shared);
        Wallet.setActive(shared ? WalletType.SHARED : WalletType.PERSONAL);
    }
}
