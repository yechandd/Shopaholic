package uk.joshiejack.shopaholic.data.shop.comparator;

import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.PlayerStatusComparator;
import uk.joshiejack.shopaholic.world.shop.comparator.TeamStatusComparator;

@SuppressWarnings("unused")
public class StatusComparatorBuilder implements ComparatorBuilder {
    private final String type;
    private final String status;

    protected StatusComparatorBuilder(String type, String status) {
        this.type = type;
        this.status = status;
    }

    @Override
    public Comparator build() {
        return type.equals("player") ? new PlayerStatusComparator(status) : new TeamStatusComparator(status);
    }
}
