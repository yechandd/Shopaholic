package uk.joshiejack.shopaholic.network.bank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.network.AbstractSetPlayerNBTPacket;

@Packet(PacketFlow.SERVERBOUND)
public class SwitchWalletPacket extends AbstractSetPlayerNBTPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("switch_wallet");
    
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
    
    private final boolean shared;

    @SuppressWarnings("unused")
    public SwitchWalletPacket(boolean shared) {
        super("ShopaholicSettings");
        this.shared = shared;
    }

    public SwitchWalletPacket(FriendlyByteBuf buf) {
        super("ShopaholicSettings");
        shared = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(shared);
    }


    @Override
    public void handleServer(ServerPlayer player) {
        super.handle(player);
        PenguinNetwork.sendToClient(player, new SetActiveWalletPacket(shared));
    }

    @Override
    public void setData(CompoundTag tag) {
        tag.putBoolean("SharedWallet", shared); //Set this data, YESSSSSSSSSSSSS
    }
}
