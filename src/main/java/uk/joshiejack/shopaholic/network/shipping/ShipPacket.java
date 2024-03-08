package uk.joshiejack.shopaholic.network.shipping;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.SyncCompoundTagPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.client.ShopaholicClientConfig;
import uk.joshiejack.shopaholic.client.renderer.TrackingRenderer;
import uk.joshiejack.shopaholic.client.shipping.Shipped;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import java.util.Set;

@Packet(PacketFlow.CLIENTBOUND)
public class ShipPacket extends SyncCompoundTagPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("ship_item");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ShipPacket(CompoundTag tag) {
        super(tag);
    }
    public ShipPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Player player) {
        Set<Shipping.SoldItem> toSell = Sets.newHashSet();
        Shipping.readHolderCollection(tag.getList("ToSell", 10), toSell);
        if (ShopaholicClientConfig.enableShippingTicker.get()) {
            player.level().playSound(player, player.xo, player.yo, player.zo, Shopaholic.ShopaholicSounds.KERCHING.value(),
                    SoundSource.PLAYERS, 1.5F, player.level().random.nextFloat() * 0.1F + 0.9F);
            NeoForge.EVENT_BUS.register(new TrackingRenderer(toSell));
        }

        //Merge in the newly sold items to the sold list
        Set<Shipping.SoldItem> merged = Sets.newHashSet();
        for (Shipping.SoldItem holder: toSell) {
            for (Shipping.SoldItem sold : Shipped.getSold()) {
                if (sold.matches(holder.getStack())) {
                    sold.merge(holder); //Merge in the holder
                    merged.add(holder);
                }
            }
        }

        Shipped.clearCountCache();
        toSell.stream().filter(s -> !merged.contains(s)).forEach(r -> Shipped.getSold().add(r));
    }

}
