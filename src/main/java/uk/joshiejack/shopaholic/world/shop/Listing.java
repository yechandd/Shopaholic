package uk.joshiejack.shopaholic.world.shop;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.logging.log4j.Level;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.random.IdentifiableWeightedRandomList;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.CostFormula;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.event.ItemPurchasedEvent;
import uk.joshiejack.shopaholic.network.shop.SyncStockLevelPacket;
import uk.joshiejack.shopaholic.world.shop.inventory.Inventory;
import uk.joshiejack.shopaholic.world.shop.inventory.Stock;
import uk.joshiejack.shopaholic.world.shop.inventory.StockMechanic;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Listing {
    public static final Codec<Listing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("unique_id", "default").forGetter(listing -> {
                if (listing.genID) return "default";
                else return listing.listing_id;
            }),
            Codec.STRING.optionalFieldOf("cost_formula", "default").forGetter(listing -> listing.costScriptID),
            ResourceLocation.CODEC.optionalFieldOf("stock_mechanic", StockMechanic.UNLIMITED_ID).forGetter(listing -> listing.stockMechanicID),
            Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(listing -> listing.conditions),
            Sublisting.CODEC.listOf().fieldOf("sublistings").forGetter(listing -> listing.sublistings.unwrap().values().stream().toList())
    ).apply(instance, Listing::new));
    protected final List<Condition> conditions = Lists.newArrayList();
    private IdentifiableWeightedRandomList<Sublisting> sublistings;
    private final List<Sublisting> listingBuilder = Lists.newArrayList();
    private final String listing_id;
    private String costScriptID;
    private ResourceLocation stockMechanicID;
    private final Lazy<CostFormula> costScript = Lazy.of(() -> ShopRegistries.COST_FORMULAE.get(costScriptID));
    private final Lazy<StockMechanic> stockMechanic = Lazy.of(() -> Shopaholic.ShopaholicRegistries.STOCK_MECHANICS.get(stockMechanicID));
    private static final RandomSource random = RandomSource.create();
    private final boolean genID;

    public Listing(String id, String costFormula, ResourceLocation mechanic, List<Condition> conditions, List<Sublisting> list) {
        this.genID = id.equals("default");
        this.listing_id = genID ? list.stream().findFirst().orElseThrow().generateID() : id;
        this.costScriptID = costFormula;
        this.stockMechanicID = mechanic;
        this.conditions.addAll(conditions);
        this.sublistings = IdentifiableWeightedRandomList.create(list.stream().collect(Collectors.toMap(Sublisting::id, sublisting -> sublisting)));
    }

    public Listing condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public Listing build() {
        this.sublistings = IdentifiableWeightedRandomList.create(listingBuilder.stream().collect(Collectors.toMap(Sublisting::id, sublisting -> sublisting)));
        return this;
    }

    public static Listing fromNetwork(FriendlyByteBuf buffer) {
        String listing_id = buffer.readUtf(32767);
        String costScriptID = buffer.readUtf(32767);
        ResourceLocation stockMechanicID = buffer.readResourceLocation();
        List<Condition> conditions = buffer.readJsonWithCodec(Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf());
        return new Listing(listing_id, costScriptID, stockMechanicID, conditions, buffer.readJsonWithCodec(Sublisting.CODEC.listOf()));
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(listing_id);
        buffer.writeUtf(costScriptID);
        buffer.writeResourceLocation(stockMechanicID);
        buffer.writeJsonWithCodec(Shopaholic.ShopaholicRegistries.CONDITION_CODEC.listOf(), conditions);
        buffer.writeJsonWithCodec(Sublisting.CODEC.listOf(), sublistings.unwrap().values().stream().toList());
    }

    public boolean isSingleEntry() {
        return sublistings.unwrap().size() == 1;
    }

    public Sublisting getSublistingByID(String id) {
        return sublistings.unwrap().get(id);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String getRandomID(RandomSource random) {
        return sublistings.getRandom(random).get().id();
    }

    public StockMechanic getStockMechanic() {
        return stockMechanic.get();
    }

    public String id() {
        return listing_id;
    }

    public long getGoldCost(Player player, Department department, Stock stock) {
        if (costScript.get() == null) { //If it is still null, then log an error
            Shopaholic.LOGGER.log(Level.ERROR, "Cost script was null for: " + department.id() + ":" + listing_id);
            return 0L;
        } else {
            random.setSeed(player.getPersistentData().getInt("ShopaholicSeed") + listing_id.hashCode() * 3643257684289L); //Get the shop seed
            long result = costScript.get().getCost(999999999L, player, getSubListing(stock), stock.getStockLevel(this), stockMechanic.get(), random);
            if (result == 999999999L) {
                Shopaholic.LOGGER.log(Level.ERROR, "Had an error while processing getCost for the item: " + department.id() + ":" + listing_id);
                return 999999999;
            } else return result;
        }
    }

    public Sublisting getSubListing(Stock stock) {
        return isSingleEntry() ? sublistings.getRandom(random).orElseThrow() : getSublistingByID(stock.getStockedObject(this));
    }

    /**
     * If this can be listed for this player
     **/
    public boolean canList(@Nonnull ShopTarget target, Department department, Stock stock) {
        return stock.getStockLevel(this) > 0
                && conditions.stream().allMatch(condition -> condition.valid(target, department, this, Condition.CheckType.SHOP_LISTING));
    }

    /**
     * If the player is able to purchase this
     * Gold has already been checked
     **/
    public boolean canPurchase(Player player, Stock stock, int amount) {
        return getSubListing(stock).hasMaterialRequirement(player, amount) && stock.getStockLevel(this) >= amount;
    }

    public void purchase(Player player, Department department) {
        Stock stock = department.getStockLevels(player.level());
        getSubListing(stock).purchase(player);
        if (player instanceof ServerPlayer serverPlayer) {
            stock.decreaseStockLevel(this);
            PenguinNetwork.sendToClient(serverPlayer, new SyncStockLevelPacket(department, this, stock.getStockLevel(this)));
            Inventory.setChanged((ServerLevel) serverPlayer.level());
        }

        conditions.forEach(c -> c.onPurchase(player, department, this));
        NeoForge.EVENT_BUS.post(new ItemPurchasedEvent(player, department, this));
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Listing listing = (Listing) o;
        return Objects.equals(listing_id, listing.listing_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listing_id);
    }

    public Listing sublisting(Sublisting listing) {
        listingBuilder.add(listing);
        return this;
    }
}
