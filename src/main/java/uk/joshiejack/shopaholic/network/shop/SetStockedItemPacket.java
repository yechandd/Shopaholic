package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.network.AbstractPacketSyncDepartment;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

@Packet(PacketFlow.CLIENTBOUND)
public class SetStockedItemPacket extends AbstractPacketSyncDepartment {
    public static final ResourceLocation ID = PenguinLib.prefix("set_stocked_item");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private final Listing listing;
    private final String stockID;

    public SetStockedItemPacket(Department department, Listing listing, String stockID) {
        super(department);
        this.listing = listing;
        this.stockID = stockID;
    }

    public SetStockedItemPacket(FriendlyByteBuf buf) {
        super(buf);
        listing = department.getListingByID(buf.readUtf());
        stockID = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeUtf(listing.id());
        buf.writeUtf(stockID);
    }

    @Override
    public void handle(Player player) {
        department.getStockLevels(player.level()).setStockedItem(listing, stockID);
    }
}