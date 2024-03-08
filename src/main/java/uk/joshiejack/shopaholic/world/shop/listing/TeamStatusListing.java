package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record TeamStatusListing(String key, Comparator value) implements ListingType {
    public static final Codec<TeamStatusListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TeamStatusListing::key),
            Comparator.CODEC.fieldOf("comparator").forGetter(TeamStatusListing::value)
    ).apply(instance, TeamStatusListing::new));
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
        CompoundTag data = PenguinTeams.getTeamForPlayer(player).getData();
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
        return "team_status:" + key;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
