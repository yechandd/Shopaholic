package uk.joshiejack.shopaholic.world.shop;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joptsimple.internal.Strings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.client.ClientStockLevels;
import uk.joshiejack.shopaholic.network.shop.SetPlayerShopSeed;
import uk.joshiejack.shopaholic.network.shop.SyncStockLevelsPacket;
import uk.joshiejack.shopaholic.world.inventory.DepartmentMenu;
import uk.joshiejack.shopaholic.world.shop.input.BlockShopInput;
import uk.joshiejack.shopaholic.world.shop.input.EntityShopInput;
import uk.joshiejack.shopaholic.world.shop.input.InputMethod;
import uk.joshiejack.shopaholic.world.shop.input.InputToShop;
import uk.joshiejack.shopaholic.world.shop.inventory.Inventory;
import uk.joshiejack.shopaholic.world.shop.inventory.Stock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Department implements ReloadableRegistry.PenguinRegistry<Department> {
    private static final ResourceLocation EMPTY_ID = new ResourceLocation(Shopaholic.MODID, "empty");
    public static final Component OUT_OF_STOCK = Component.translatable("gui.shopaholic.shop.outofstock");

    public enum ShopTargetType implements StringRepresentable {
        BLOCK, ENTITY, ITEM, COMMAND;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static final Codec<Department> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("shop").forGetter(department -> department.shopID),
            StringRepresentable.fromEnum(ShopTargetType::values).fieldOf("vendor_type").forGetter(department -> department.targetType),
            Codec.STRING.fieldOf("vendor_data").forGetter(department -> department.targetData),
            StringRepresentable.fromEnum(InputMethod::values).fieldOf("method").forGetter(department -> department.method),
            ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(department -> department.name),
            ComponentSerialization.CODEC.optionalFieldOf("out_of_stock", OUT_OF_STOCK).forGetter(department -> department.outOfStockText),
            Icon.CODEC.optionalFieldOf("icon", ItemIcon.EMPTY).forGetter(department -> department.icon),
            Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(department -> department.conditions),
            Listing.CODEC.listOf().optionalFieldOf("listings", Collections.emptyList()).forGetter(department -> department.getListings().isEmpty() ?
                    Collections.emptyList() : new ArrayList<>(department.getListings()))
            //
    ).apply(instance, Department::new));

    public static final Department EMPTY = new Department(EMPTY_ID, ShopTargetType.COMMAND, Strings.EMPTY, InputMethod.COMMAND, Component.empty(),
            OUT_OF_STOCK, ItemIcon.EMPTY, Collections.emptyList(), Collections.emptyList());
    private final Map<String, Listing> listings = Maps.newLinkedHashMap();
    private final List<Condition> conditions = new ArrayList<>();
    //Initialise with default values
    private ResourceLocation shopID = EMPTY_ID;
    private Icon icon = ItemIcon.EMPTY;
    private Component name = Component.empty();
    private InputMethod method = InputMethod.COMMAND;
    private ShopTargetType targetType = ShopTargetType.ENTITY;
    private String targetData = Strings.EMPTY;
    private Component outOfStockText = OUT_OF_STOCK;
    private final Lazy<Shop> shop = Lazy.of(() -> Shopaholic.ShopaholicRegistries.SHOPS.get(shopID));

    @Deprecated
    public Department() {}
    public Department(Department department) {
        this.shopID = department.shopID;
        this.method = department.method;
        this.targetType = department.targetType;
        this.targetData = department.targetData;
        this.name = department.name;
        this.outOfStockText = department.outOfStockText;
        this.icon = department.icon;
        this.listings.putAll(department.listings);
        this.conditions.addAll(department.conditions);
    }

    public Department(ResourceLocation shop, ShopTargetType targetType, String targetData, InputMethod method, Component name, Component outOfStock, Icon icon, List<Condition> conditions, List<Listing> listings) {
        this.shopID = shop;
        this.method = method;
        this.targetType = targetType;
        this.targetData = targetData;
        this.name = name;
        this.outOfStockText = outOfStock;
        this.icon = icon;
        this.conditions.addAll(conditions);
        listings.forEach(this::addListing);
        InputToShop.register(targetType.getSerializedName(), targetData, this);
    }

    public Department shop(@Nonnull ResourceLocation shop) {
        this.shopID = shop;
        return this;
    }

    public Stock getStockLevels(Level world) {
        return world instanceof ServerLevel serverLevel ? Inventory.getStock(serverLevel, this) : ClientStockLevels.getStock(this);
    }

    @Nullable
    public Shop getShop() {
        return shop.get();
    }

    @Override
    public ResourceLocation id() {
        ResourceLocation id = Shopaholic.ShopaholicRegistries.DEPARTMENTS.getID(this);
        return id == null ? new ResourceLocation(Shopaholic.MODID, "empty") : id;
    }

    @Override
    public Department fromNetwork(FriendlyByteBuf buf) {
        return new Department(buf.readResourceLocation(),
                buf.readEnum(ShopTargetType.class),
                buf.readUtf(32767),
                buf.readEnum(InputMethod.class),
                buf.readComponent(),
                buf.readComponent(),
                Icon.fromNetwork(buf),
                buf.readJsonWithCodec(Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf()),
                buf.readCollection(ArrayList::new, Listing::fromNetwork));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(shopID);
        buf.writeEnum(targetType);
        buf.writeUtf(targetData, 32767);
        buf.writeEnum(method);
        buf.writeComponent(name);
        buf.writeComponent(outOfStockText);
        icon.toNetwork(buf);
        buf.writeJsonWithCodec(Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf(), conditions);
        buf.writeCollection(listings.values(), (buffer, listing) -> listing.toNetwork(buffer));
//        buf.writeEnum(method);
//        buf.writeEnum(vendor);
//        buf.writeUtf(vendorData, 32767);
//        buf.writeComponent(name);
//        buf.writeComponent(outOfStockText);
//        icon.toNetwork(buf);
//        buf.writeJsonWithCodec(Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf(), conditions);
//        buf.writeCollection(listings.values(), (buffer, listing) -> listing.toNetwork(buffer));
    }

    public Department name(Component name) {
        this.name = name;
        return this;
    }

    public Department icon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public void addListing(Listing listing) {
        listings.put(listing.id(), listing);
    }
    public Listing getListingByID(String id) {
        return listings.get(id);
    }

    public Collection<Listing> getListings() {
        return listings.values();
    }

    public Icon getIcon() {
        return icon;
    }

    public Component getName() {
        return name;
    }

    public Component getOutOfStockText() {
        return outOfStockText == null ? OUT_OF_STOCK : outOfStockText;
    }

    public boolean isValidFor(ShopTarget target, Condition.CheckType type, InputMethod method) {
        return this.method == method && isValidFor(target, type);
    }

    public boolean isValidFor(@Nullable ShopTarget target, Condition.CheckType type) {
        return target != null && conditions.stream().allMatch(condition -> condition.valid(target, type));
    }

    public void open(ShopTarget target, boolean reloadLastDepartment) {
        //Sync supermarket inventories too
        Shop market = Shop.get(this);
        if (market != null) {
            market.getDepartments().forEach(department ->
                    PenguinNetwork.sendToClient((ServerPlayer) target.getPlayer(), new SyncStockLevelsPacket(department, department.getStockLevels(target.getLevel()))));
        } else
            PenguinNetwork.sendToClient((ServerPlayer) target.getPlayer(), new SyncStockLevelsPacket(this, getStockLevels(target.getLevel()))); //Sync the stock levels to the player
        /* Seed the random shopness */
        int seed = 13 * (1 + TimeHelper.getElapsedDays(target.getLevel().getDayTime()));
        target.getPlayer().getPersistentData().putInt("ShopaholicSeed", seed);
        PenguinNetwork.sendToClient((ServerPlayer) target.getPlayer(), new SetPlayerShopSeed(seed));
        /* Open the shop */ //Open after the stock level has been received
        target.getPlayer().openMenu(new SimpleMenuProvider((id, inv, player) ->
                        new DepartmentMenu(id, player, null)
                                .withData(this, target, reloadLastDepartment), name),
                buf -> {
                    buf.writeBoolean(reloadLastDepartment);
                    buf.writeResourceLocation(id());
                    buf.writeVarLong(target.getPos().asLong());
                    buf.writeVarInt(target.getEntity().getId());
                    buf.writeItem(target.getStack());
                    buf.writeByte(target.getInput().intID());
                    if (target.getInput() instanceof BlockShopInput) buf.writeByte(0);
                    else if (target.getInput() instanceof EntityShopInput) buf.writeByte(1);
                    else buf.writeByte(2);
                    target.getInput().write(buf);
                });
    }
}
