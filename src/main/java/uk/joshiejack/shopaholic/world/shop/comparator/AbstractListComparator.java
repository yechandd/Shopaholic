package uk.joshiejack.shopaholic.world.shop.comparator;

import com.google.common.collect.Lists;
import uk.joshiejack.shopaholic.api.shop.Comparator;

import java.util.List;

public abstract class AbstractListComparator implements Comparator {
    protected final List<Comparator> comparators = Lists.newArrayList();
}
