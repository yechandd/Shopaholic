package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.TextureIcon;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.world.shop.ShopLoader;

//Doesn't do anything, just displays the gold as the listing type, for selling item purposes
public record GoldListing() implements ListingType {
    public static final GoldListing INSTANCE = new GoldListing();
    public static final Codec<GoldListing> CODEC = Codec.unit(INSTANCE);

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return ShopaholicClient.EMPTY_STRING;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Icon createIcon() {
        return new TextureIcon(ShopLoader.EXTRA, 240, 224, 1);
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @Override
    public void purchase(Player player) {}

    @Override
    public String id() {
        return "gold";
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
