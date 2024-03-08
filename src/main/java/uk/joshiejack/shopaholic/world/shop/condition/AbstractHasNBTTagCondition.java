package uk.joshiejack.shopaholic.world.shop.condition;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public abstract class AbstractHasNBTTagCondition<T> extends AbstractPredicateCondition<T> {
    protected CompoundTag tag;

    protected abstract Predicate<T> createPredicate(CompoundTag CompoundTag);
}