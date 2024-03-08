package uk.joshiejack.shopaholic.world.shop.condition;

import com.google.common.collect.Lists;
import uk.joshiejack.shopaholic.api.shop.Condition;

import java.util.List;

public abstract class AbstractListCondition implements Condition {
    protected final List<Condition> conditions = Lists.newArrayList();
}
