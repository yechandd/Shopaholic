package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.protocol.PacketFlow;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.network.AbstractPacketSyncDepartment;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.inventory.Stock;

import javax.annotation.Nonnull;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncStockLevelsPacket extends AbstractPacketSyncDepartment {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_stock_levels");
    @Override
    public @Nonnull ResourceLocation id() {
        return ID;
    }
    private final CompoundTag data;

    @SuppressWarnings("unused")
    public SyncStockLevelsPacket(Department department, Stock stock) {
        super(department);
        this.data = stock.serializeNBT();
    }

    public SyncStockLevelsPacket(FriendlyByteBuf from) {
        super(from);
        data = from.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf to) {        
        super.write(to);
        to.writeNbt(data);
    }


    @OnlyIn(Dist.CLIENT)
    public void handleClientPacket() {
        department.getStockLevels(Minecraft.getInstance().level).deserializeNBT(data);
        ShopaholicClient.refreshShop(); //Stock levels have changed
    }
}
