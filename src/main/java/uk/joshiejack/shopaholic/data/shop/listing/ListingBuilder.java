package uk.joshiejack.shopaholic.data.shop.listing;

import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.world.shop.Listing;
import uk.joshiejack.shopaholic.world.shop.Sublisting;
import uk.joshiejack.shopaholic.world.shop.inventory.StockMechanic;

import java.util.ArrayList;
import java.util.List;

public class ListingBuilder {
    public final String id;
    public ResourceLocation stockMechanic = StockMechanic.UNLIMITED_ID;
    public String costFormula = "default";
    public final List<Condition> conditions = new ArrayList<>();
    public final List<SublistingBuilder<?>> sublistings = new ArrayList<>();

    public Listing build() {
        return new Listing(id, costFormula, stockMechanic,
                conditions,
                buildSublistingList());
    }

    private List<Sublisting> buildSublistingList() {
        return sublistings.stream().map(SublistingBuilder::build).toList();
    }

    public ListingBuilder(String id) {
        this.id = id;
    }

    public static ListingBuilder withId(String id) {
        return new ListingBuilder(id);
    }

    public ListingBuilder with(SublistingBuilder<?> sublisting) {
        if (!sublistings.isEmpty()) {
            if (sublistings.size() == 1)
                sublistings.get(0).id("item_1");
            sublisting.id("item_" + (sublistings.size() + 1));
        }

        sublistings.add(sublisting);
        return this;
    }

    public ListingBuilder stockMechanic(ResourceLocation stockMechanic) {
        this.stockMechanic = stockMechanic;
        return this;
    }

    public ListingBuilder costFormula(String costFormula) {
        this.costFormula = costFormula;
        return this;
    }

    public ListingBuilder condition(Condition condition) {
        conditions.add(condition);
        return this;
    }
}
