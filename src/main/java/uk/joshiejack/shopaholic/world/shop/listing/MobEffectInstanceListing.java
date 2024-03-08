package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.api.shop.ListingType;

import java.util.Objects;

public record MobEffectInstanceListing(MobEffectInstance effect) implements ListingType {
    public static final Codec<MobEffectInstanceListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(handler -> handler.effect.getEffect()),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", 160).forGetter(handler -> handler.effect.getDuration()),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("amplifier", 0).forGetter(handler -> handler.effect.getAmplifier()),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(handler -> handler.effect.isAmbient()),
            Codec.BOOL.optionalFieldOf("particles", true).forGetter(handler -> handler.effect.isVisible())
    ).apply(instance, (effect, duration, amplifier, ambient, particles) ->
            new MobEffectInstanceListing(new MobEffectInstance(effect, duration, amplifier, ambient, particles))));

    public static ListingType of(MobEffectInstance effect) {
        return new MobEffectInstanceListing(effect);
    }

    @Override
    public Icon createIcon() {
        return new ItemIcon(new ItemStack(Items.POTION));
    }

    @Override
    public void purchase(Player player) {
        if (player.hasEffect(effect.getEffect()))
            Objects.requireNonNull(player.getEffect(effect.getEffect())).duration += effect.getDuration();
        player.addEffect(new MobEffectInstance(effect)); //COPY!
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return effect.getEffect().getDisplayName();
    }

    @Override
    public String id() {
        return RegistryHelper.id(effect.getEffect()).toString();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isValid() {
        return effect.getEffect() != null;
    }
}
