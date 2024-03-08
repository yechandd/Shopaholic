package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public record VendorHealthComparator() implements Comparator {
    public static final VendorHealthComparator INSTANCE = new VendorHealthComparator();
    public static final Codec<VendorHealthComparator> CODEC = Codec.unit(INSTANCE);

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@NotNull ShopTarget target) {
        return target.getEntity() instanceof LivingEntity ? (int) ((LivingEntity) target.getEntity()).getHealth() : 0;
    }
}