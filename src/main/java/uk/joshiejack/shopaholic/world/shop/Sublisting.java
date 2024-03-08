package uk.joshiejack.shopaholic.world.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.ISublisting;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.world.shop.listing.ItemStackListing;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sublisting implements WeightedEntry, ISublisting {
    public static final Weight ONE = Weight.of(1);

    public static final Codec<Sublisting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("sub_id", "default").forGetter(sublisting -> sublisting.sub_id),
            Shopaholic.ShopaholicRegistries.LISTING_TYPE_CODEC.fieldOf("listing").forGetter(sublisting -> sublisting.processor),
            MaterialCost.CODEC.listOf().optionalFieldOf("materials", Collections.emptyList()).forGetter(sublisting -> sublisting.materials),
            ComponentSerialization.CODEC.listOf().optionalFieldOf("tooltip", Collections.emptyList()).forGetter(sublisting -> sublisting.tooltip),
            Icon.CODEC.optionalFieldOf("icon", ItemIcon.EMPTY).forGetter(sublisting -> sublisting.icon),
            ComponentSerialization.CODEC.optionalFieldOf("display_name", Component.empty()).forGetter(sublisting -> sublisting.displayName),
            Codec.LONG.optionalFieldOf("gold", 0L).forGetter(sublisting -> sublisting.gold),
            Weight.CODEC.optionalFieldOf("weight", ONE).forGetter(sublisting -> sublisting.weight)
    ).apply(instance, Sublisting::new));
    //Fuck me this becomes complicated


    private final String sub_id;
    private final List<MaterialCost> materials;
    private final List<Component> tooltip;
    private Icon icon;
    private final Component displayName;
    private long gold;
    private final Weight weight;
    private final ListingType processor;

    public Sublisting(String s, ListingType listingHandler, List<MaterialCost> materialCosts, List<Component> tooltip, Icon icon, Component name, Long gold, Weight weight) {
        this.sub_id = s;
        this.processor = listingHandler;
        this.materials = materialCosts;
        this.tooltip = tooltip;
        this.icon = icon;
        this.displayName = name;
        this.gold = gold;
        this.weight = weight;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public String id() {
        return sub_id;
    }

    public @NotNull Weight getWeight() {
        return weight;
    }

    @Override
    public long getGold() {
        return gold;
    }

    public List<MaterialCost> getMaterials() {
        return materials;
    }

    public void purchase(Player player) {
        processor.purchase(player);
        if (materials != null) {
            materials.forEach(m -> m.fulfill(player));
        }
    }

    public boolean isValid() {
        return processor.isValid();
    }

    @OnlyIn(Dist.CLIENT)
    public Component getDisplayName() { //TODO CHeck this?
        return !displayName.getString().isEmpty() ? displayName : gold < 0 ? DepartmentScreen.getCostAsTextComponent(Math.abs(gold)) : processor.getDisplayName();
    }

    public boolean isGoldOnly() {
        return materials == null || materials.isEmpty();
    }

    @Override
    public ItemStack getItem() {
        return processor instanceof ItemStackListing ? ((ItemStackListing) processor).get() : ItemStack.EMPTY;
    }

    public Icon getIcon() {
        if (icon == ItemIcon.EMPTY) {
            icon = processor.createIcon();
        }

        return icon;
    }

    public int getCount() {
        return gold < 0 ? (int) Math.abs(gold) : processor.getCount();
    }

    @OnlyIn(Dist.CLIENT)
    public void addTooltip(List<Component> list) {
        if (tooltip == null)
            processor.addTooltip(list);
         else
            list.addAll(tooltip);
    }

    public boolean hasMaterialRequirement(Player player, int amount) {
        return materials != null && materials.stream().allMatch(m -> m.isMet(player, amount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sublisting that = (Sublisting) o;
        return Objects.equals(sub_id, that.sub_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sub_id);
    }

    public String generateID() {
        return processor.id();
    }

    public ListingType getProcessor() {
        return processor;
    }
}
