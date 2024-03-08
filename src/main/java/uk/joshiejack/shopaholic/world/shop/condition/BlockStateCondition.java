package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;

public class BlockStateCondition implements Condition {
    public static final Codec<BlockStateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("property").forGetter(condition -> condition.property),
            Codec.STRING.fieldOf("days").forGetter(condition -> condition.value)
    ).apply(instance, BlockStateCondition::new));


    private String property;
    private String value;

    public BlockStateCondition(String property, String value) {
        this.property = property;
        this.value = value;
    }

    @SuppressWarnings("unused")
    public static Condition blockState(String stateName, String stateValue) {
        return new BlockStateCondition(stateName, stateValue);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        if (type == CheckType.SHOP_LISTING) return false;
        else {
            BlockState state = target.getLevel().getBlockState(target.getPos());
            Property<?> property = state.getBlock().getStateDefinition().getProperty(this.property);
            return property != null && state.getValue(property).toString().equals(value);
        }
    }
}
