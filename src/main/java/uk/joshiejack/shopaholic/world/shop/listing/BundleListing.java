package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ListIcon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.ListingType;

import java.util.List;

public class BundleListing implements ListingType {
    public static final Codec<BundleListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Shopaholic.ShopaholicRegistries.LISTING_TYPE_CODEC.listOf().fieldOf("list").forGetter(handler -> handler.list)
    ).apply(instance, BundleListing::new));
    private final List<ListingType> list;

    public BundleListing(List<ListingType> list) {
        this.list = list;
    }

    public static ListingType of(ListingType... list) {
        return new BundleListing(List.of(list));
    }

    @Override
    public Icon createIcon() {
        return new ListIcon(list.stream().map(ListingType::createIcon).toList(), 1);
    }

    @Override
    public void purchase(Player player) {
        list.forEach(v -> v.purchase(player));
    }

    /* todo? */
    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addTooltip(List<Component> list) {
        this.list.forEach(p -> p.addTooltip(list));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        ListingType handle = list.get(0);
        return handle.getDisplayName();
    }

    @Override
    public String id() {
        return "bundle:" + list.get(0).id();
    }

    @Override
    public boolean isValid() {
        return !list.isEmpty() && list.stream().allMatch(ListingType::isValid);
    }
}
