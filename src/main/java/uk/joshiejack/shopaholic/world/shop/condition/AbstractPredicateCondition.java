package uk.joshiejack.shopaholic.world.shop.condition;

import uk.joshiejack.shopaholic.api.shop.Condition;

import java.util.function.Predicate;

public abstract class AbstractPredicateCondition<T> implements Condition {
    protected Predicate<T> predicate;
}

