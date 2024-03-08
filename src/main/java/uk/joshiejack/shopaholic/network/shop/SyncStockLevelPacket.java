package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.protocol.PacketFlow;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.network.AbstractPacketSyncDepartment;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

import javax.annotation.Nonnull;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncStockLevelPacket extends AbstractPacketSyncDepartment {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_stock_level");
    @Override
    public @Nonnull ResourceLocation id() {
        return ID;
    }

    private final Listing listing;
    private final int stock;

    @SuppressWarnings("unused")
    public SyncStockLevelPacket(Department department, Listing listing, int stock) {
        super(department);
        this.listing = listing;
        this.stock = stock;
    }

    public SyncStockLevelPacket(FriendlyByteBuf from) {
        super(from);
        listing = department.getListingByID(from.readUtf());
        stock = from.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        super.write(to);
        to.writeUtf(listing.id());
        to.writeVarInt(stock);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClient() {
        department.getStockLevels(Minecraft.getInstance().level).setStockLevel(listing, stock);
    }
}
