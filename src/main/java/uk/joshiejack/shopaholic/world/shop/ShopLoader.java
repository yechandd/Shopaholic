package uk.joshiejack.shopaholic.world.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.shopaholic.Shopaholic;

//@SuppressWarnings("rawtypes")
//@Mod.EventBusSubscriber(modid = Shopaholic.MODID)
public class ShopLoader {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(Shopaholic.MODID, "textures/gui/shop.png");
    public static final ResourceLocation EXTRA = new ResourceLocation(Shopaholic.MODID, "textures/gui/shop_extra.png");

    /*@SubscribeEvent
    public static void onDatabaseCollected(DatabasePopulateEvent event) {
        //Update the database with the simple departments
        //Very simple listings AND Generic one for any types
        ShopRegistries.LISTING_TYPES.forEach((type, handler) -> {
            event.table("very_simple_department_listings_" + type).rows().forEach(sdl -> registerSimpleListing(event, sdl, sdl.get(type), type, sdl.get(type), "unlimited"));
            event.table("simple_department_listings_" + type).rows().forEach(sdl -> registerSimpleListing(event, sdl, sdl.get("id"), type, sdl.get(type), sdl.get("stock mechanic")));
        });

        event.table("very_simple_department_listings").rows().forEach(sdl -> registerSimpleListing(event, sdl, sdl.get("data"), sdl.get("type"), sdl.get("data"), "unlimited"));
        event.table("simple_department_listings").rows().forEach(sdl -> registerSimpleListing(event, sdl, sdl.get("id"), sdl.get("type"), sdl.get("data"), sdl.get("stock mechanic")));

        event.table("very_simple_shops").rows().forEach(vss -> {
            String name = vss.name();
            String id = name.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "_");
            event.createTable("shops", "ID,Name,Background Texture,Extra Texture,Vendor ID,Opening Method")
                    .insert(id, name, "default", "default", id, "right_click");
            event.createTable("vendors", "ID,Type,Data").insert(id, vss.get("type").toString(), vss.get("data"));
        });

        event.table("simple_shops").rows().forEach(ss -> event.createTable("shops", "ID,Name,Background Texture,Extra Texture,Vendor ID,Opening Method")
                .insert(ss.id(), ss.name(), "default", "default", ss.get("vendor id"), "right_click"));
    }

    private static void registerSimpleListing(DatabasePopulateEvent database, Row sdl, String id, String type, String data, String stock_mechanic) {
        String departmentID = sdl.get("department id");
        long gold = sdl.getAsLong("gold");
        database.createTable("department_listings", "Department ID", "ID", "Stock Mechanic", "Cost Formula").insert(departmentID, id, stock_mechanic, "default");
        database.createTable("sublistings", "Department ID", "Listing ID", "ID", "Type", "Data", "Gold", "Weight").insert(departmentID, id, "default", type, data, gold, 1);
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) { //LOW, So we appear after recipes have been added
        //Clear out all existing shop related data
        Shop.clear(); //Clear out the department -> shop mappings
        //Department.REGISTRY.clear(); //Clear out all the registered departments
//        InputToShop.BLOCK_TO_SHOP.clear(); //Clear the block - > shop mappings
//        InputToShop.ENTITY_TO_SHOP.clear(); //Clear the entity - > shop mappings
//        InputToShop.ITEM_TO_SHOP.clear(); //Clear the item - > shop mappings
//        //TODO: Clear out the inputs BEFORE database Event

        ShopLoadingData data = new ShopLoadingData();

        //Temporary Registry, Create all the comparators
        //for all the type of comparators
        ShopRegistries.COMPARATORS.forEach((type, comparator) -> {
            if (comparator instanceof MutableComparator) {
                event.table("comparator_" + type).rows().forEach(row -> {
                    String comparatorID = row.id();
                    if (data.comparators.containsKey(comparatorID)) {
                        ((MutableComparator) data.comparators.get(comparatorID)).merge(row);
                    } else {
                        Comparator theComparator = ShopRegistries.COMPARATORS.get(type);
                        if (theComparator != null) {
                            data.comparators.put(comparatorID, ((MutableComparator) theComparator).create(row, comparatorID));
                        }
                    }
                });
            } else
                data.comparators.put(type, comparator);
        });

        //Create all the conditions
        for (String type : ShopRegistries.CONDITIONS.keySet()) {
            event.table("condition_" + type).rows().forEach(condition -> {
                String conditionID = condition.id();
                if (data.conditions.containsKey(conditionID)) {
                    data.conditions.get(conditionID).merge(condition);
                } else {
                    Condition theCondition = ShopRegistries.CONDITIONS.get(type);
                    if (theCondition != null)
                        data.conditions.put(conditionID, theCondition.create(data, condition, conditionID));
                }
            });
        }

        data.comparators.entrySet().stream().filter(e -> e.getValue() instanceof AbstractListComparator).forEach(entry ->
                ((AbstractListComparator) entry).initList(event, entry.getKey(), data.comparators));
        data.conditions.entrySet().stream().filter(e -> e.getValue() instanceof AbstractListCondition).forEach(entry ->
                ((AbstractListCondition) entry.getValue()).initList(event, entry.getKey(), data.conditions));

        //Create all the stock mechanics
        event.table("stock_mechanics").rows().forEach(stock_mechanic ->
                data.stock_mechanics.put(stock_mechanic.id(), new StockMechanic(stock_mechanic.get("max stock"), stock_mechanic.get("replenish rate"))));

        //JsonParser parser = new JsonParser();
        event.table("shops").rows().forEach(shop -> {
            String shopID = shop.id();
            String name = shop.name();
            String vendorID = shop.get("vendor id");
            ResourceLocation background = shop.isEmpty("background texture") ? DEFAULT_BACKGROUND : shop.getRL("background texture");
            ResourceLocation extra = shop.isEmpty("extra texture") ? EXTRA : shop.getRL("extra texture");
            InputMethod opening_method = InputMethod.valueOf(shop.get("opening method").toString().toUpperCase(Locale.ENGLISH));
            Shop theShop = new Shop(Component.translatable(name), background, extra);
            //Hopefully in time?
            Shopaholic.ShopaholicRegistries.SHOPS.registry().put(new ResourceLocation(Shopaholic.MODID, shopID), theShop);
            event.table("departments").where("shop id=" + shopID).forEach(department -> {
                String departmentID = department.id();
                Department theDepartment = new Department(theShop, opening_method); //Add the department to the shop in creation
                //Register the department to the actual reloadable Registries? hopefully in time xD
                Shopaholic.ShopaholicRegistries.DEPARTMENTS.registry().put(new ResourceLocation(Shopaholic.MODID, departmentID), theDepartment);
                if (!department.isEmpty("icon")) {
                    String icon = department.get("icon").toString();
                    if (icon.contains("\"")) {
                        theDepartment.icon(Util.getOrThrow(Icon.CODEC.parse(JsonOps.INSTANCE, GsonHelper.fromJson(gson, icon, JsonElement.class)), JsonParseException::new));
                    } else theDepartment.icon(new ItemIcon(department.icon()));
                }

                if (!department.isEmpty("name")) theDepartment.setName(department.name());
                Row vendor = event.table("vendors").fetch_where("id=" + vendorID); //Register the vendor
                if (vendor == null)
                    Shopaholic.LOGGER.error(String.format("Could not find the vendor id: %s. The department %s may not function correctly.", vendorID, departmentID));
                else
                    InputToShop.register(vendor.get("type"), vendor.get("data").toString(), theDepartment); //to the input

                //Add the conditions for this shop
                event.table("shop_conditions").where("shop id=" + shopID)
                        .forEach(condition -> theDepartment.addCondition(data.conditions.get(condition.get("condition id").toString())));
                event.table("department_conditions").where("shop id=" + shopID + "&department id=" + departmentID)
                        .forEach(condition -> theDepartment.addCondition(data.conditions.get(condition.get("condition id").toString())));

                event.table("department_listings").where("department id=" + departmentID).forEach(listing -> {
                    String listingID = listing.id();
                    String dataID = listingID.contains("$") ? listingID.split("\\$")[0] : listingID;
                    List<Sublisting<?>> sublistings = Lists.newArrayList();//handler.getObjectsFromDatabase(database.get(), department_id, data_id);
                    event.table("sublistings").where("department id=" + departmentID + "&listing id=" + dataID).forEach(sublisting -> {
                        String originalSubID = sublisting.id();
                        String originalSubType = sublisting.get("type");
                        boolean builder = originalSubType.endsWith("_builder");
                        String sub_type = originalSubType.replace("_builder", ""); //Remove the builder
                        List<String> data_entries = builder ? ListingBuilder.BUILDERS.get(sublisting.get("data").toString()).items() : Lists.newArrayList(sublisting.get("data").toString());
                        for (int i = 0; i < data_entries.size(); i++) {
                            String subID = builder ? originalSubID + "$" + i : originalSubID;
                            ListingType.Builder handler = ShopRegistries.LISTING_TYPES.get(sub_type);
                            if (handler == null)
                                Shopaholic.LOGGER.log(Level.ERROR, "Failed to find the listing handler type: " + sub_type + " for " + departmentID + ": " + listingID + " :" + subID);
                            else {
                                Sublisting<?> theSublisting = new Sublisting(subID, handler.build(data, event, data_entries.get(i)));
                                theSublisting.setGold(sublisting.getAsLong("gold"));
                                theSublisting.setWeight(sublisting.getAsDouble("weight"));
                                String materialID = subID.contains("$") ? subID.split("\\$")[0] : subID;
                                event.table("sublisting_materials").where("department id=" + departmentID + "&listing id=" + dataID + "&sub id=" + materialID).forEach(material -> theSublisting.addMaterial(new MaterialCost(material.get("item").toString(), material.getAsInt("amount"))));

                                //Load in display overrides
                                Row display = event.table("sublisting_display_data").fetch_where("department id=" + departmentID + "&listing id=" + dataID + "&sub id=" + materialID);
                                if (!display.isEmpty("icon")) {
                                    String icon = display.get("icon").toString();
                                    if (icon.contains("\"")) {
                                        theSublisting.setIcon(Util.getOrThrow(Icon.CODEC.parse(JsonOps.INSTANCE, GsonHelper.fromJson(gson, icon, JsonElement.class)), JsonParseException::new));
                                    } else theSublisting.setIcon(new ItemIcon(display.icon()));
                                }

                                if (!display.isEmpty("name")) theSublisting.setDisplayName(display.get("name"));
                                if (!display.isEmpty("tooltip"))
                                    theSublisting.setTooltip(StringEscapeUtils.unescapeJava(display.get("tooltip").toString()).split("\n"));
                                sublistings.add(theSublisting);
                            }
                        }
                    });

                    if (!sublistings.isEmpty() && sublistings.stream().allMatch(sublisting -> sublisting.isValid())) {
                        StockMechanic stockMechanic = data.stock_mechanics.get(listing.get("stock mechanic").toString());
                        CostFormula costScript = ShopRegistries.COST_FORMULAE.get(listing.get("cost formula").toString());
                        if (stockMechanic == null)
                            Shopaholic.LOGGER.log(Level.ERROR, "Failed to find the stock mechanic: " + listing.get("stock mechanic") + " for " + departmentID + ":" + listingID);
                        else if (costScript == null)
                            Shopaholic.LOGGER.log(Level.ERROR, "Failed to find the cost script: " + listing.get("cost formula") + " for " + departmentID + ":" + listingID);
                        else {
                            Listing theListing = new Listing(listingID, sublistings, costScript, stockMechanic);
                            theDepartment.addListing(theListing);
                            event.table("listing_conditions").where("department id=" + departmentID + "&listing id=" + dataID)
                                    .forEach(condition -> {
                                        Condition cd = data.conditions.get(condition.get("condition id").toString());
                                        if (cd == null)
                                            Shopaholic.LOGGER.error("Incorrect condition added as a listing condition with the id: "
                                                    + condition.get("condition id") + " with the listing id " + listingID + " in the department " + departmentID);
                                        else
                                            theListing.addCondition(data.conditions.get(condition.get("condition id").toString()));
                                    });
                            Shopaholic.LOGGER.log(Level.INFO, "Successfully added the listing: " + listingID + " for " + departmentID);
                        }
                    } else if (sublistings.isEmpty()) {
                        Shopaholic.LOGGER.log(Level.ERROR, "No sublistings were added for the listing: " + departmentID + ":" + listingID);
                    } else {
                        for (Sublisting sublisting : sublistings) {
                            if (!sublisting.isValid()) {
                                Shopaholic.LOGGER.log(Level.ERROR, "The sublisting: " + sublisting.id() + " created an invalid object for the listing: " + departmentID + ":" + listingID);
                            }
                        }
                    }
                });
            });
        });
    }*/
}
