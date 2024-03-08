package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.network.AbstractPurchaseItemPacket;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

@SuppressWarnings("unused")
@Packet(PacketFlow.CLIENTBOUND)
public class ClientPurchaseItemPacket extends AbstractPurchaseItemPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("client_purchase_item");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ClientPurchaseItemPacket(Department department, Listing listing, int amount) {
        super(department, listing, amount);
    }

    public ClientPurchaseItemPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void handle(Player player) {
        for (int i = 0; i < amount; i++) {
            listing.purchase(player, department);
        }
    }
}
