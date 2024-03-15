package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weight;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.data.shop.comparator.ComparatorBuilder;
import uk.joshiejack.shopaholic.world.shop.MaterialCost;
import uk.joshiejack.shopaholic.world.shop.Sublisting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class SublistingBuilder<O> {
    public static final Component EMPTY = Component.empty();
    public final List<ItemStack> materials = new ArrayList<>();
    public final List<Pair<TagKey<Item>, Integer>> tagMaterials = new ArrayList<>();
    public final O data;
    public String id = "default";
    public long gold = 0;
    public Weight weight = Sublisting.ONE;
    public Component name = EMPTY;
    public Icon icon = ItemIcon.EMPTY;
    public List<Component> tooltip = Collections.emptyList();

    public SublistingBuilder(O data) {
        this.data = data;
    }

    public abstract Sublisting build();

    public static ItemListingBuilder itemListing(Item item) {
        return new ItemListingBuilder(item.getDefaultInstance());
    }

    public static ItemListingBuilder itemListing(ItemStack stack) {
        return new ItemListingBuilder(stack);
    }

    public static MobEffectListingBuilder potionListing(MobEffectInstance effect) {
        return new MobEffectListingBuilder(effect);
    }

    public static EntityListingBuilder entityListing(EntityType<?> spawnData) {
        return new EntityListingBuilder(spawnData);
    }

    public static DepartmentListingBuilder departmentListing(ResourceLocation departmentID) {
        return new DepartmentListingBuilder(departmentID);
    }

    public static BundleListingBuilder bundleListing() {
        return new BundleListingBuilder();
    }

    public static HealListingBuilder healListing(float healAmount) {
        return new HealListingBuilder(healAmount);
    }

    public static CommandListingBuilder commandListing(String command) {
        return new CommandListingBuilder(command);
    }

    public static KubeJSScriptListingBuilder kubejs(String script) {
        return new KubeJSScriptListingBuilder(script);
    }

    public static StatusListingBuilder playerStatusListing(String field, ComparatorBuilder comparator) {
        return new StatusListingBuilder("player_status", field, comparator);
    }

    public static StatusListingBuilder teamStatusListing(String field, ComparatorBuilder comparator) {
        return new StatusListingBuilder("team_status", field, comparator);
    }

    public static GoldListingBuilder sellListing() {
        return new GoldListingBuilder();
    }

    public void id(String id) {
        this.id = id;
    }

    public SublistingBuilder<O> cost(int cost) {
        gold = cost;
        return this;
    }

    public SublistingBuilder<O> weight(int weight) {
        this.weight = Weight.of(weight);
        return this;
    }

    public SublistingBuilder<O> material(Item item, int count) {
        materials.add(new ItemStack(item, count));
        return this;
    }

    public SublistingBuilder<O> material(TagKey<Item> stack, int count) {
        tagMaterials.add(Pair.of(stack, count));
        return this;
    }

    public SublistingBuilder<O> name(String name) {
        this.name = Component.literal(name);
        return this;
    }

    public SublistingBuilder<O> icon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public SublistingBuilder<O> tooltip(Component... tooltip) {
        this.tooltip = List.of(tooltip);
        return this;
    }

    public SublistingBuilder<O> tooltip(String string) {
        this.tooltip = Arrays.stream(string.split("\n")).map(Component::literal).collect(Collectors.toList());
        return this;
    }

    public List<MaterialCost> buildMaterials() {
        if (materials.isEmpty() && tagMaterials.isEmpty()) Collections.emptyList();
        List<MaterialCost> costs = new ArrayList<>();
        for (ItemStack stack: materials) {
            costs.add(new MaterialCost(Ingredient.of(stack), 1));
        }

        for (Pair<TagKey<Item>, Integer> pair: tagMaterials) {
            costs.add(new MaterialCost(Ingredient.of(pair.getLeft()), pair.getRight()));
        }

        return costs;
    }
}
