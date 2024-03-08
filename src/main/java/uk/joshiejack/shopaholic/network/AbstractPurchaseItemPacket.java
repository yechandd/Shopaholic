package uk.joshiejack.shopaholic.network;

import net.minecraft.network.FriendlyByteBuf;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

public abstract class AbstractPurchaseItemPacket extends AbstractPacketSyncDepartment {
    protected Listing listing;
    protected int amount;

    public AbstractPurchaseItemPacket(Department department, Listing listing, int amount) {
        super(department);
        this.listing = listing;
        this.amount = amount;
    }

    public AbstractPurchaseItemPacket(FriendlyByteBuf buf) {
        super(buf);
        listing = department.getListingByID(buf.readUtf(32767));
        amount =  buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeUtf(listing.id());
        buf.writeInt(amount);
    }
}