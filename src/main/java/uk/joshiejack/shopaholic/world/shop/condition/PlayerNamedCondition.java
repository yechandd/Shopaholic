package uk.joshiejack.shopaholic.world.shop.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public record PlayerNamedCondition(Pattern pattern) implements Condition {
    public static final Codec<PlayerNamedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.PATTERN.fieldOf("pattern").forGetter(PlayerNamedCondition::pattern)
    ).apply(instance, PlayerNamedCondition::new));

    public static Condition named(String name) {
        return new PlayerNamedCondition(Pattern.compile(name));
    }


    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean valid(@Nonnull ShopTarget target, @Nonnull CheckType type) {
        return pattern.matcher(target.getPlayer().getName().getString()).matches();
    }
}
