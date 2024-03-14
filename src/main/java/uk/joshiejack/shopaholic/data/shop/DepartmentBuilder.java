package uk.joshiejack.shopaholic.data.shop;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.data.shop.listing.ListingBuilder;
import uk.joshiejack.shopaholic.data.shop.listing.SublistingBuilder;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

import java.util.ArrayList;
import java.util.List;

public class DepartmentBuilder {
    public final ResourceLocation id;
    public final Component name;
    public final Icon icon;
    public final List<Condition> conditions = new ArrayList<>();
    public final List<ListingBuilder> listings = new ArrayList<>();
    public Component outofstock;

    public DepartmentBuilder(ResourceLocation id, Component name, Icon icon) {
        this.id = id;
        this.name = name;
        this.outofstock = Department.OUT_OF_STOCK;
        this.icon = icon;
    }

    public List<Condition> buildConditions() {
        return conditions;
    }

    public List<Listing> buildListings() {
        return listings.stream().map(ListingBuilder::build).toList();
    }

    public static DepartmentBuilder of(ResourceLocation id, Icon icon, Component name) {
        return new DepartmentBuilder(id, name, icon);
    }

    public DepartmentBuilder listing(ListingBuilder listing) {
        this.listings.add(listing);
        return this;
    }

    public DepartmentBuilder condition(Condition condition) {
        conditions.add(condition);
        return this;
    }

    //Shorthands
    public DepartmentBuilder entityListing(EntityType<?> entity, int gold, Condition... conditions) {
        if (entity == null)
            throw new IllegalArgumentException("Cannot add a null entity to the shop");
        ListingBuilder builder = ListingBuilder.of(RegistryHelper.id(entity).getPath())
                .addSublisting(SublistingBuilder.entity(entity).cost(gold));
        for (Condition condition : conditions) {
            builder.condition(condition);
        }

        return listing(builder);
    }

    public DepartmentBuilder itemListing(Item item, int gold, Condition... conditions) {
        if (item == null)
            throw new IllegalArgumentException("Cannot add a null item to the shop");
        return itemListing(item.getDefaultInstance(), gold, conditions);
    }

    public DepartmentBuilder itemListing(ItemStack item, int gold, Condition... conditions) {
        return itemListing(RegistryHelper.id(item.getItem()).getPath(), item, gold, conditions);
    }

    public DepartmentBuilder itemListing(String id, ItemStack item, int gold, Condition... conditions) {
        if (item.isEmpty())
            throw new IllegalArgumentException("Cannot add an empty item to the shop");
        ListingBuilder builder = ListingBuilder.of(id)
                .addSublisting(SublistingBuilder.item(item).cost(gold));
        for (Condition condition : conditions) {
            builder.condition(condition);
        }

        return listing(builder);
    }

    public DepartmentBuilder sellListing(Item item, int gold) {
        listing(ListingBuilder.of("sell_" + RegistryHelper.id(item).getPath())
                .addSublisting(SublistingBuilder.sell().cost(-gold).material(item, 1)));
        return this;
    }

    public DepartmentBuilder sellListing(TagKey<Item> item, int gold) {
        listing(ListingBuilder.of("sell_" + item.location().getPath())
                .addSublisting(SublistingBuilder.sell().cost(-gold).material(item, 1)));
        return this;
    }
}
