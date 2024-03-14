package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record PlayerStatusListing(String key, Comparator value) implements ListingType {
    public static final Codec<PlayerStatusListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(PlayerStatusListing::key),
            Shopaholic.ShopaholicRegistries.COMPARATOR_CODEC.fieldOf("comparator").forGetter(PlayerStatusListing::value)
    ).apply(instance, PlayerStatusListing::new));
    private static final Component EMPTY = Component.empty();

    @OnlyIn(Dist.CLIENT)
    @Override
    public Icon createIcon() {
        return CommandListing.ICON.get();
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @Override
    public void purchase(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains("PenguinStatuses"))
            data.put("PenguinStatuses", new CompoundTag());
        data.getCompound("PenguinStatuses").putInt(key, value.getValue(ShopTarget.fromPlayer(player)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return EMPTY;
    }

    @Override
    public String id() {
        return "player_status:" + key;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
