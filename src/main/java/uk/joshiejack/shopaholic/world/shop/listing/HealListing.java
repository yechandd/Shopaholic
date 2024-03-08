package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.TextureIcon;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.world.shop.ShopLoader;

public record HealListing(float amount) implements ListingType {
    private static final Component NAME = Component.translatable("gui.shopaholic.shop.heal");
    public static final Codec<HealListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount").forGetter(handler -> handler.amount)
    ).apply(instance, HealListing::new));

    @Override
    public Icon createIcon() {
        return new TextureIcon(ShopLoader.EXTRA, 192, 240, 1);
    }

    @Override
    public void purchase(Player player) {
        if (!player.level().isClientSide)
            player.heal(amount);
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return NAME;
    }

    @Override
    public String id() {
        return "heal:" + amount;
    }

    @Override
    public boolean isValid() {
        return amount > 0;
    }
}
