package uk.joshiejack.shopaholic.network.shipping;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.client.shipping.Shipped;
import uk.joshiejack.shopaholic.network.AbstractSyncSoldItemList;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import java.util.Set;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncLastSoldPacket extends AbstractSyncSoldItemList {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_last_sold");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    @SuppressWarnings("unused")
    public SyncLastSoldPacket(Set<Shipping.SoldItem> set) {
        super(set);
    }
    public SyncLastSoldPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

                              @Override
    public void handle(ListTag listNBT) {
        Shipping.readHolderCollection(listNBT, Shipped.getShippingLog());
    }
}
