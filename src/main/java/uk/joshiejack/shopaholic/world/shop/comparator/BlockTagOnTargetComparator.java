package uk.joshiejack.shopaholic.world.shop.comparator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public record BlockTagOnTargetComparator(TagKey<Block> tag) implements Comparator {
    public static final Codec<BlockTagOnTargetComparator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(inst -> inst.tag)
    ).apply(instance, BlockTagOnTargetComparator::new));

    @Override
    public Codec<? extends Comparator> codec() {
        return CODEC;
    }

    @Override
    public int getValue(@Nonnull ShopTarget target) {
        return target.getLevel().getBlockState(target.getPos()).is(tag) ? 1 : 0;
    }
}