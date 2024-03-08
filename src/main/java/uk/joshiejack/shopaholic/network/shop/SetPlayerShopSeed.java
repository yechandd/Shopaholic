package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;

@Packet(PacketFlow.CLIENTBOUND)
public class SetPlayerShopSeed implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_player_shop_seed");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private final int seed;

    public SetPlayerShopSeed(int value) {
        this.seed = value;
    }

    public SetPlayerShopSeed(FriendlyByteBuf from) {
        seed = from.readVarInt();
    }


    @Override
    public void write(FriendlyByteBuf to) {
        to.writeVarInt(seed);
    }


    @Override
    public void handle(Player player) {
        player.getPersistentData().putInt("ShopaholicSeed", seed);
    }
}
