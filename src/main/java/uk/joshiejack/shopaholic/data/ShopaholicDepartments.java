package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.data.shop.DepartmentBuilder;
import uk.joshiejack.shopaholic.data.shop.ShopBuilder;
import uk.joshiejack.shopaholic.data.shop.Vendor;
import uk.joshiejack.shopaholic.data.shop.listing.ListingBuilder;
import uk.joshiejack.shopaholic.data.shop.listing.SublistingBuilder;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.condition.NamedCondition;
import uk.joshiejack.shopaholic.world.shop.input.InputMethod;

import java.util.Map;

public class ShopaholicDepartments extends AbstractPenguinRegistryProvider<Department> {
    public ShopaholicDepartments(PackOutput output) {
        super(output, Shopaholic.ShopaholicRegistries.DEPARTMENTS);
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, Department> map) {
        ShopBuilder.of(ShopaholicShops.PIGGY_BANK_V_2)
                .vendor(Vendor.entity("pig", EntityType.PIG))
                .condition(NamedCondition.named("George"))
                .openWith(InputMethod.RIGHT_CLICK)
                .department(DepartmentBuilder.of(Shopaholic.prefix("test_department"), new ItemIcon(Items.GOLD_INGOT.getDefaultInstance()), Component.literal("Gold Store"))
                        .listing(ListingBuilder.of("apple").addSublisting(SublistingBuilder.item(Items.APPLE).cost(200).icon(new ItemIcon(Items.ACACIA_PLANKS.getDefaultInstance()))))
                        .listing(ListingBuilder.of("bucket").addSublisting(SublistingBuilder.item(Items.BUCKET).cost(100)))
                        .listing(ListingBuilder.of("poison").addSublisting(SublistingBuilder.potion(new MobEffectInstance(MobEffects.POISON, 1000))))
                        .listing(ListingBuilder.of("food").addSublisting(SublistingBuilder.bundle()
                                .addToBundle(SublistingBuilder.item(Items.CARROT))
                                .addToBundle(SublistingBuilder.item(Items.POTATO))
                                .addToBundle(SublistingBuilder.item(Items.BEETROOT))
                                .cost(9000)
                                .name("Food Bundle")
                                .tooltip("A delicious bundle of food.\nContains\n*Apple\n*Carrot\n*Potato")))
                )
                .department(DepartmentBuilder.of(Shopaholic.prefix("crafting_central"), new ItemIcon(Items.CRAFTING_TABLE.getDefaultInstance()), Component.literal("Crafting Central"))
                        .listing(ListingBuilder.of("furnace")
                                .addSublisting(SublistingBuilder.item(Items.FURNACE)
                                        .material(Tags.Items.COBBLESTONE, 8)))
                        .listing(ListingBuilder.of("stairs")
                                .stockMechanic(ShopaholicStockMechanics.ONLY_ONE)
                                .addSublisting(SublistingBuilder.item(Items.PISTON)
                                        .material(Tags.Items.COBBLESTONE, 6)
                                        .material(Tags.Items.DUSTS_REDSTONE, 1)
                                        .material(ItemTags.PLANKS, 2)
                                        .material(Tags.Items.INGOTS_IRON, 1)))
                        .listing(ListingBuilder.of("piggy_bank").addSublisting(SublistingBuilder.department(Shopaholic.prefix("test_department"))))
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
