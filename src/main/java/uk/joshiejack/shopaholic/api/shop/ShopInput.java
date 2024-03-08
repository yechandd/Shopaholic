package uk.joshiejack.shopaholic.api.shop;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public abstract class ShopInput<T> {
    protected final DefaultedRegistry<T> registry;
    protected final T id;

    protected ShopInput(DefaultedRegistry<T> registry, T id) {
        this.id = id;
        this.registry = registry;
    }

    protected ShopInput(DefaultedRegistry<T> registry, FriendlyByteBuf buf) {
        this.registry = registry;
        this.id = buf.readById(registry);
    }

    public abstract int intID();

    public void write(FriendlyByteBuf buf) {
        buf.writeId(registry, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopInput<?> input = (ShopInput<?>) o;
        return Objects.equals(registry.getKey(id), registry.getKey((T) input.id));
    }

    public T get() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registry.getKey(id));
    }

    public String getName(ShopTarget target) {
        return Objects.requireNonNull(registry.getKey(id)).toString();
    }
}
