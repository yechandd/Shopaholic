package uk.joshiejack.shopaholic.network;

import net.minecraft.network.FriendlyByteBuf;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.world.shop.Department;

public abstract class AbstractPacketSyncDepartment implements PenguinPacket {
    protected final Department department;

    public AbstractPacketSyncDepartment(Department department) {
        this.department = department;
    }

    public AbstractPacketSyncDepartment(FriendlyByteBuf buf) {
        department = Shopaholic.ShopaholicRegistries.DEPARTMENTS.get(buf.readResourceLocation());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(department.id());
    }
}
