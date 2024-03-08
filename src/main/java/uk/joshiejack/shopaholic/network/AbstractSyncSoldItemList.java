package uk.joshiejack.shopaholic.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import java.util.Set;

public abstract class AbstractSyncSoldItemList implements PenguinPacket {
    private final ListTag listNBT;

    public AbstractSyncSoldItemList(Set<Shipping.SoldItem> set) {
        listNBT = Shipping.writeHolderCollection(set);
    }

    public AbstractSyncSoldItemList(FriendlyByteBuf from) {
        CompoundTag tag = from.readNbt();
        assert tag != null;
        listNBT = tag.getList("list", 10);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        CompoundTag tag = new CompoundTag();
        tag.put("list", listNBT);
        to.writeNbt(tag);
    }

    @Override
    public void handle(Player player) {
        handle(listNBT);
    }

    protected abstract void handle(ListTag list);
}
