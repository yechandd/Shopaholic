package uk.joshiejack.shopaholic.world.shop;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joptsimple.internal.Strings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.penguinlib.util.icon.ListIcon;

import java.util.stream.Collectors;

public class MaterialCost {
    public static final Codec<MaterialCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("item").forGetter(m -> m.ingredient.get()),
            Codec.INT.fieldOf("cost").forGetter(MaterialCost::getCost)
    ).apply(instance, MaterialCost::new));
    private Lazy<Ingredient> ingredient = Lazy.of(() -> getItem().startsWith("#") ?
            Ingredient.of(ItemTags.create(new ResourceLocation(getItem().startsWith("#") ? getItem().substring(1) : getItem().substring(4))))
            : Ingredient.of(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(getItem())))));
    private Lazy<Icon> icon = Lazy.of(() -> new ListIcon(Lists.newArrayList(ingredient.get().getItems()).stream().map(ItemIcon::new).collect(Collectors.toList()), 1));
    private String item = Strings.EMPTY;
    private final int cost;

    public MaterialCost(Ingredient ingredient, int cost) {
        this.ingredient = Lazy.of(() -> ingredient);
        this.icon = Lazy.of(() -> new ListIcon(Lists.newArrayList(ingredient.getItems()).stream().map(ItemIcon::new).collect(Collectors.toList()), 1));
        this.cost = cost;
    }

    public MaterialCost(String item, int cost) {
        this.item = item;
        this.cost = cost;
    }

    private String getItem() {
        return item;
    }

    public int getCost() {
        return cost;
    }

    public Icon getIcon() {
        return icon.get();
    }

    public boolean isMet(Player player, int amount) {
        return PlayerHelper.hasInInventory(player, ingredient.get(), (cost * amount));
    }

    public void fulfill(Player player) {
        PlayerHelper.takeFromInventory(player, ingredient.get(), cost);
    }
}
