package uk.joshiejack.shopaholic.data.shop.comparator;

import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.NumberComparator;

public class NumberComparatorBuilder implements ComparatorBuilder {
    private final int number;

    protected NumberComparatorBuilder(int number) {
        this.number = number;
    }

    @Override
    public Comparator build() {
        return new NumberComparator(number);
    }
}
