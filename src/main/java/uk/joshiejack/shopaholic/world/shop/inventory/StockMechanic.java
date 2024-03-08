package uk.joshiejack.shopaholic.world.shop.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.IStockMechanic;

public record StockMechanic(int maximum, int increase) implements ReloadableRegistry.PenguinRegistry<StockMechanic>, IStockMechanic {
    public static final Codec<StockMechanic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("maximum").forGetter(StockMechanic::maximum),
            Codec.INT.fieldOf("increase").forGetter(StockMechanic::increase)
    ).apply(instance, StockMechanic::new));

    public static final ResourceLocation UNLIMITED_ID = new ResourceLocation(Shopaholic.MODID, "unlimited");
    public static final StockMechanic UNLIMITED = new StockMechanic(1000000000, 1000000000);

    @Override
    public ResourceLocation id() {
        if (this == UNLIMITED) return UNLIMITED_ID; //Special case the unlimited id
        return Shopaholic.ShopaholicRegistries.STOCK_MECHANICS.getID(this);
    }

    @Override
    public StockMechanic fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new StockMechanic(friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(maximum);
        friendlyByteBuf.writeInt(increase);
    }
}