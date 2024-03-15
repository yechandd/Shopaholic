package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.data.shop.ShopBuilder;
import uk.joshiejack.shopaholic.data.shop.listing.SublistingBuilder;
import uk.joshiejack.shopaholic.world.shop.Department;

import java.util.Map;

import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.effect.MobEffects.POISON;
import static net.minecraft.world.item.Items.*;
import static uk.joshiejack.shopaholic.Shopaholic.prefix;
import static uk.joshiejack.shopaholic.data.ShopaholicShops.PIGGY_BANK_V_2;
import static uk.joshiejack.shopaholic.data.ShopaholicStockMechanics.ONLY_ONE;
import static uk.joshiejack.shopaholic.data.shop.DepartmentBuilder.department;
import static uk.joshiejack.shopaholic.data.shop.Vendor.entityVendor;
import static uk.joshiejack.shopaholic.data.shop.listing.ListingBuilder.withId;
import static uk.joshiejack.shopaholic.data.shop.listing.SublistingBuilder.departmentListing;
import static uk.joshiejack.shopaholic.data.shop.listing.SublistingBuilder.itemListing;
import static uk.joshiejack.shopaholic.world.shop.condition.NamedCondition.named;
import static uk.joshiejack.shopaholic.world.shop.input.InputMethod.RIGHT_CLICK;

public class ShopaholicDepartments extends AbstractPenguinRegistryProvider<Department> {
    public ShopaholicDepartments(PackOutput output) {
        super(output, Shopaholic.ShopaholicRegistries.DEPARTMENTS);
    }

    public static ItemIcon itemIcon(ItemLike item) {
        return new ItemIcon(item.asItem().getDefaultInstance());
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, Department> map) {
        ShopBuilder.of(PIGGY_BANK_V_2)
                .vendor(entityVendor(EntityType.PIG))
                .condition(named("George"))
                .openWith(RIGHT_CLICK)
                .department(department(prefix("test_department"), itemIcon(GOLD_INGOT), literal("Gold Store"))
                        .listing(withId("apple").with(itemListing(APPLE).cost(200).icon(itemIcon(ACACIA_PLANKS))))
                        .listing(withId("bucket").with(itemListing(BUCKET).cost(100)))
                        .listing(withId("poison").with(SublistingBuilder.potionListing(new MobEffectInstance(POISON, 1000))))
                        .listing(withId("food").with(SublistingBuilder.bundleListing()
                                .addToBundle(itemListing(CARROT))
                                .addToBundle(itemListing(POTATO))
                                .addToBundle(itemListing(BEETROOT))
                                .cost(9000)
                                .name("Food Bundle")
                                .tooltip("A delicious bundle of food.\nContains\n*Apple\n*Carrot\n*Potato")))
                )
                .department(department(prefix("crafting_central"), itemIcon(CRAFTING_TABLE), literal("Crafting Central"))
                        .listing(withId("furnace")
                                .with(itemListing(FURNACE)
                                        .material(Tags.Items.COBBLESTONE, 8)))
                        .listing(withId("stairs")
                                .stockMechanic(ONLY_ONE)
                                .with(itemListing(PISTON)
                                        .material(Tags.Items.COBBLESTONE, 6)
                                        .material(Tags.Items.DUSTS_REDSTONE, 1)
                                        .material(ItemTags.PLANKS, 2)
                                        .material(Tags.Items.INGOTS_IRON, 1)))
                        .listing(withId("piggy_bank").with(departmentListing(prefix("test_department"))))
                )
                .save(map);
//        ShopBuilder.of(ShopaholicShops.PIGGY_BANK_V_2, map)
//                .vendor(Vendor.entity(EntityType.PIG))
//                .openWith(InputMethod.RIGHT_CLICK)
//                .department(Shopaholic.prefix("test_department_1"),
//                        new Department()
//                                .icon(new ItemIcon(Items.IRON_INGOT.getDefaultInstance()))
//                                .name(Component.literal("Iron Department"))
//                                .listing(Listing.of(Sublisting.of(ItemStackListing.of(Items.APPLE))
//                                        .cost(250)
//                                        .icon(new ItemIcon(Items.DARK_OAK_BOAT.getDefaultInstance()))))
//
//                                .listing(Listing.of(Sublisting.of(ItemStackListing.of(Items.BUCKET))
//                                        .cost(125)))
//                                .listing(Listing.of(Sublisting.of(MobEffectInstanceListing.of(new MobEffectInstance(MobEffects.WITHER, 1000)))))
//                                .listing(Listing.of(Sublisting.of(BundleListing.of(
//                                                        ItemStackListing.of(Items.APPLE),
//                                                        ItemStackListing.of(Items.BUCKET),
//                                                        MobEffectInstanceListing.of(new MobEffectInstance(MobEffects.WITHER, 1000))
//                                                ))
//                                                .cost(11000)
//                                                .name(Component.literal("Bundle of Fun"))
//                                                .tooltip(Component.literal("This is a bundle of fun!"),
//                                                        Component.literal("Contains:"),
//                                                        Component.literal("1x Apple"),
//                                                        Component.literal("1x Bucket"),
//                                                        Component.literal("1x Wither Effect"))
//
//                                )));
    }
}
