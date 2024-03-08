package uk.joshiejack.shopaholic.world.shop;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.shopaholic.Shopaholic;

import javax.annotation.Nullable;
import java.util.List;

public class Shop implements ReloadableRegistry.PenguinRegistry<Shop> {
    //Codec
    public static final Codec<Shop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(shop -> shop.name),
            ResourceLocation.CODEC.optionalFieldOf("background", ShopLoader.DEFAULT_BACKGROUND).forGetter(s -> s.background),
            ResourceLocation.CODEC.optionalFieldOf("extra", ShopLoader.EXTRA).forGetter(s -> s.extra)
    ).apply(instance, Shop::new));

    public static final Shop EMPTY = new Shop(Component.literal("empty"), ShopLoader.DEFAULT_BACKGROUND, ShopLoader.EXTRA);
    private final List<Department> departments = Lists.newArrayList();
    private final Component name;
    private final ResourceLocation background;
    private final ResourceLocation extra;
    private @Nullable Department last;

    public Shop(Shop shop) {
        this.name = shop.name;
        this.background = shop.background;
        this.extra = shop.extra;
    }

    public Shop(Component name, ResourceLocation background, ResourceLocation extra) {
        this.name = name;
        this.background = background;
        this.extra = extra;
    }

    public static void clear() {
        //DEPARTMENT_TO_SHOP.clear();
    }

    @Override
    public ResourceLocation id() {
        return Shopaholic.ShopaholicRegistries.SHOPS.getID(this);
    }

    @Override
    public Shop fromNetwork(FriendlyByteBuf buf) {
        return new Shop(buf.readComponent(), buf.readResourceLocation(), buf.readResourceLocation());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeComponent(name);
        buf.writeResourceLocation(background);
        buf.writeResourceLocation(extra);
    }

    public Component getLocalizedName() {
        return name;
    }

    public List<Department> getDepartments() {
        if (departments.isEmpty()) {
            Shopaholic.ShopaholicRegistries.DEPARTMENTS.stream()
                    .filter(department -> department.getShop() == this)
                    .forEach(departments::add);
        }

        return departments;
    }

    public static Shop get(Department shop) {
        Shop market = shop.getShop();
        if (market != null && market.last == null) {
            market.last = shop;
        }

        return market;
    }

    public Department getLast() {
        return last;
    }

    public void setLast(Department last) {
        this.last = last;
    }

    public ResourceLocation getBackground() {
        return background;
    }

    public ResourceLocation getExtra() {
        return extra;
    }
}
