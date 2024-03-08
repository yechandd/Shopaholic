package uk.joshiejack.shopaholic.data.shop.comparator;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.shopaholic.api.shop.Comparator;
import uk.joshiejack.shopaholic.world.shop.comparator.BlockTagOnTargetComparator;

public class BlockTagOnTargetComparatorBuilder implements ComparatorBuilder {
    private final TagKey<Block> tag;

    @SuppressWarnings("unused")
    protected BlockTagOnTargetComparatorBuilder(TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    public Comparator build() {
        return new BlockTagOnTargetComparator(tag);
    }
}
