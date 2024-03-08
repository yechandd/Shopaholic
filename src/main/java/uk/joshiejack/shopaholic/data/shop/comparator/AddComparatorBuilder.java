package uk.joshiejack.shopaholic.data.shop.comparator;

import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.AddComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Conditions
public class AddComparatorBuilder implements ComparatorBuilder {
    protected final List<ComparatorBuilder> comparators = new ArrayList<>();

    @SuppressWarnings("unused")
    public AddComparatorBuilder(ComparatorBuilder... comparators) {
        this.comparators.addAll(Arrays.asList(comparators));
    }

    @Override
    public Comparator build() {
        return new AddComparator(comparators.stream().map(ComparatorBuilder::build).toList());
    }
}
