package uk.joshiejack.shopaholic;

import com.mojang.serialization.Codec;
import net.minecraft.DetectedVersion;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.penguinlib.world.item.BookItem;
import uk.joshiejack.shopaholic.api.ShopaholicAPI;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.client.ShopaholicClientConfig;
import uk.joshiejack.shopaholic.data.*;
import uk.joshiejack.shopaholic.world.bank.Bank;
import uk.joshiejack.shopaholic.world.bank.BankAPIImpl;
import uk.joshiejack.shopaholic.world.inventory.EconomyManagerMenu;
import uk.joshiejack.shopaholic.world.inventory.ShopaholicMenus;
import uk.joshiejack.shopaholic.world.loot.ShopaholicLoot;
import uk.joshiejack.shopaholic.world.shipping.Market;
import uk.joshiejack.shopaholic.world.shipping.ShippingRegistry;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.RegistryImpl;
import uk.joshiejack.shopaholic.world.shop.Shop;
import uk.joshiejack.shopaholic.world.shop.comparator.*;
import uk.joshiejack.shopaholic.world.shop.condition.*;
import uk.joshiejack.shopaholic.world.shop.inventory.StockMechanic;
import uk.joshiejack.shopaholic.world.shop.listing.*;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(Shopaholic.MODID)
public class Shopaholic {
    public static final String MODID = "shopaholic";
    public static final Logger LOGGER = LogManager.getLogger();

    public Shopaholic(IEventBus eventBus) {
        eventBus.addListener(this::setupCommon);
        ShopaholicMenus.CONTAINERS.register(eventBus);
        ShopaholicItems.ITEMS.register(eventBus);
        ShopaholicSounds.SOUNDS.register(eventBus);
        ShopaholicLoot.LOOT_FUNCTION_TYPES.register(eventBus);
        ShopaholicAPI.registry = new RegistryImpl();
        ShopaholicAPI.bank = new BankAPIImpl();
        ShopaholicRegistries.CONDITIONS.register(eventBus);
        ShopaholicRegistries.COMPARATORS.register(eventBus);
        ShopaholicRegistries.LISTING_TYPES.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShopaholicClientConfig.create());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ShopaholicServerConfig.create());
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        //Cost Formulae
        ShopaholicAPI.registry.registerCostFormula("default", (m, player, listing, level, mechanic, rand) -> listing.getGold());
        ShopaholicAPI.registry.registerCostFormula("decreasing_cost", (e, player, listing, level, mechanic, rand) -> listing.getGold() * (1 + (level / mechanic.maximum())));
        ShopaholicAPI.registry.registerCostFormula("increasing_cost", (e, player, listing, level, mechanic, rand) -> listing.getGold() * (1 + (1 - level / mechanic.maximum())));
        ShopaholicAPI.registry.registerCostFormula("shipping_value", (e, player, listing, level, mechanic, rand) -> ShippingRegistry.getValue(listing.getItem()));
    }

    @SubscribeEvent
    public static void onDataGathering(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        generator.addProvider(event.includeClient(), new ShopaholicBlockStates(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ShopaholicLanguage(output));
        generator.addProvider(event.includeClient(), new ShopaholicItemModels(output, event.getExistingFileHelper()));
        //.addProvider(event.includeServer(), new ShopaholicDatabase(output));
        generator.addProvider(event.includeServer(), new ShopaholicStockMechanics(output));
        generator.addProvider(event.includeServer(), new ShopaholicShops(output));
        generator.addProvider(event.includeServer(), new ShopaholicDepartments(output));
        //PackMetadataGenerator
        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Resources for Shopaholic"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
    }

    @SubscribeEvent
    public static void addBookToUtilityTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(ShopaholicItems.ECONOMY_MANAGER.get());
        }
    }

    public static class ShopaholicRegistries {
        //Stock mechanics should be loaded before shops and shops before departments
        /* Conditions */
        public static final DeferredRegister<Codec<? extends Condition>> CONDITIONS = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(Shopaholic.MODID, "conditions")), Shopaholic.MODID);
        public static final Registry<Codec<? extends Condition>> CONDITION = CONDITIONS.makeRegistry(b -> b.sync(true));
        public static final Codec<Condition> CONDITION_CODEC = Shopaholic.ShopaholicRegistries.CONDITION.byNameCodec().dispatchStable(Condition::codec, Function.identity());
        public static final Holder<Codec<? extends Condition>> AND = CONDITIONS.register("and", () -> AndCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> BLOCK_ENTITY_HAS_NBT_TAG = CONDITIONS.register("block_entity_has_nbt_tag", () -> BlockEntityHasNBTTagCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> BLOCK_STATE = CONDITIONS.register("block_state", () -> BlockStateCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> COMPARE = CONDITIONS.register("compare", () -> CompareCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> ENTITY_HAS_NBT_TAG = CONDITIONS.register("entity_has_nbt_tag", () -> EntityHasNBTTagCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> ENTITY_NEARBY = CONDITIONS.register("entity_nearby", () -> EntityNearbyCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> HAS_PET = CONDITIONS.register("has_pet", () -> HasPetCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> IN_DIMENSION = CONDITIONS.register("in_dimension", () -> InDimensionCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> ITEM_HAS_NBT_TAG = CONDITIONS.register("item_has_nbt_tag", () -> ItemStackHasNBTTagCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> NAMED = CONDITIONS.register("named", () -> NamedCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> NOT = CONDITIONS.register("not", () -> NotCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> OPENING_HOURS = CONDITIONS.register("opening_hours", () -> OpeningHoursCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> OR = CONDITIONS.register("or", () -> OrCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> PER_PLAYER = CONDITIONS.register("per_player", () -> PerPlayerCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> PLAYER_NAMED = CONDITIONS.register("player_named", () -> PlayerNamedCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> SHIPPED = CONDITIONS.register("shipped", () -> ShippedCondition.CODEC);
        public static final Holder<Codec<? extends Condition>> TIME = CONDITIONS.register("time", () -> TimeCondition.CODEC);

        /* Comparators */
        public static final DeferredRegister<Codec<? extends Comparator>> COMPARATORS = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(Shopaholic.MODID, "comparators")), Shopaholic.MODID);
        public static final Registry<Codec<? extends Comparator>> COMPARATOR = COMPARATORS.makeRegistry(b -> b.sync(true));
        public static final Codec<Comparator> COMPARATOR_CODEC = ShopaholicRegistries.COMPARATOR.byNameCodec().dispatchStable(Comparator::codec, Function.identity());
        public static final Holder<Codec<? extends Comparator>> ADD = COMPARATORS.register("add", () -> AddComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> BLOCK_TAG_ON_TARGET = COMPARATORS.register("block_tag_on_target", () -> BlockTagOnTargetComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> CAN_SEE_SKY = COMPARATORS.register("can_see_sky", () -> CanSeeSkyComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> ITEM_IN_HAND = COMPARATORS.register("item_in_hand", () -> ItemInHandComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> ITEM_IN_INVENTORY = COMPARATORS.register("item_in_inventory", () -> ItemInInventoryComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> LIGHT_LEVEL = COMPARATORS.register("light_level", () -> LightLevelComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> NUMBER = COMPARATORS.register("number", () -> NumberComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> PLAYER_HEALTH = COMPARATORS.register("player_health", () -> PlayerHealthComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> PLAYER_STATUS = COMPARATORS.register("player_status", () -> PlayerStatusComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> PLAYER_X = COMPARATORS.register("player_x", () -> PlayerPositionComparator.X.CODEC);
        public static final Holder<Codec<? extends Comparator>> PLAYER_Y = COMPARATORS.register("player_y", () -> PlayerPositionComparator.Y.CODEC);
        public static final Holder<Codec<? extends Comparator>> PLAYER_Z = COMPARATORS.register("player_z", () -> PlayerPositionComparator.Z.CODEC);
        public static final Holder<Codec<? extends Comparator>> RAIN_LEVEL = COMPARATORS.register("rain_level", () -> RainLevelComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> REDSTONE_LEVEL = COMPARATORS.register("redstone_level", () -> RedstoneLevelComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> SHIPPED_COUNT = COMPARATORS.register("shipped_count", () -> ShippedCountComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> TEAM_STATUS = COMPARATORS.register("team_status", () -> TeamStatusComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> TEMPERATURE = COMPARATORS.register("temperature", () -> TemperatureComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> VENDOR_HEALTH = COMPARATORS.register("vendor_health", () -> VendorHealthComparator.CODEC);
        public static final Holder<Codec<? extends Comparator>> VENDOR_X = COMPARATORS.register("vendor_x", () -> VendorPositionComparator.X.CODEC);
        public static final Holder<Codec<? extends Comparator>> VENDOR_Y = COMPARATORS.register("vendor_y", () -> VendorPositionComparator.Y.CODEC);
        public static final Holder<Codec<? extends Comparator>> VENDOR_Z = COMPARATORS.register("vendor_z", () -> VendorPositionComparator.Z.CODEC);

        /* Listing Handlers */
        public static final DeferredRegister<Codec<? extends ListingType>> LISTING_TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(Shopaholic.MODID, "listing_handler")), Shopaholic.MODID);
        public static final Registry<Codec<? extends ListingType>> LISTING_TYPE = LISTING_TYPES.makeRegistry(b -> b.sync(true));
        public static final Codec<ListingType> LISTING_TYPE_CODEC = LISTING_TYPE.byNameCodec().dispatchStable(ListingType::codec, Function.identity());
        public static final Holder<Codec<? extends ListingType>> BUNDLE = LISTING_TYPES.register("bundle", () -> BundleListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> COMMAND = LISTING_TYPES.register("command", () -> CommandListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> DEPARTMENT = LISTING_TYPES.register("department", () -> DepartmentListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> ENTITY = LISTING_TYPES.register("entity", () -> EntityTypeListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> GOLD = LISTING_TYPES.register("gold", () -> GoldListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> HEAL = LISTING_TYPES.register("heal", () -> HealListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> ITEM = LISTING_TYPES.register("item", () -> ItemStackListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> POTION = LISTING_TYPES.register("mob_effect", () -> MobEffectInstanceListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> PLAYER_STATUS_LISTING = LISTING_TYPES.register("player_status", () -> PlayerStatusListing.CODEC);
        public static final Holder<Codec<? extends ListingType>> TEAM_STATUS_LISTING = LISTING_TYPES.register("team_status", () -> TeamStatusListing.CODEC);

        public static final ReloadableRegistry<StockMechanic> STOCK_MECHANICS = new ReloadableRegistry<>(Shopaholic.MODID, "stock_mechanics", StockMechanic.CODEC, StockMechanic.UNLIMITED, true).withPriority(EventPriority.HIGHEST);
        public static final ReloadableRegistry<Shop> SHOPS = new ReloadableRegistry<>(Shopaholic.MODID, "shops", Shop.CODEC, Shop.EMPTY, true).withPriority(EventPriority.HIGH);
        public static final ReloadableRegistry<Department> DEPARTMENTS = new ReloadableRegistry<>(Shopaholic.MODID, "departments", Department.CODEC, Department.EMPTY, true);
        //public static final Codec<List<Condition>> LIST_CONDITIONS = Shopaholic.ShopaholicRegistries.CONDITION.byNameCodec().dispatchStable(Condition::codec, Function.identity()).listOf();
        public static void init() {}
    }

    public static class ShopaholicItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Shopaholic.MODID);

        public static final DeferredItem<Item> ECONOMY_MANAGER = ITEMS.register("economy_manager", () -> new BookItem(new Item.Properties().stacksTo(1),
                () -> new SimpleMenuProvider((id, inv, p) -> new EconomyManagerMenu(id), Component.translatable("%s.item.economy_manager".formatted(Shopaholic.MODID)))));

    }

    public static class ShopaholicSounds {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
        public static final Holder<SoundEvent> KERCHING = createSoundEvent("kerching");

        private static DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(String name) {
            return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, name)));
        }
    }

    @Mod.EventBusSubscriber(modid = Shopaholic.MODID)
    public static class Sync {
        @SubscribeEvent
        public static void onPlayerJoinedWorld(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                Bank.get(serverLevel).syncToPlayer((ServerPlayer) event.getEntity());
                Market.get(serverLevel).getShippingForPlayer(event.getEntity()).syncToPlayer((ServerPlayer) event.getEntity());
            }
        }
    }
}

