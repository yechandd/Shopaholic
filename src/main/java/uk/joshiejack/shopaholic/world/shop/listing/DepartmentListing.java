package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.ListingType;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.input.EntityShopInput;

public record DepartmentListing(ResourceLocation rl) implements ListingType {
    public static final Codec<DepartmentListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("department").forGetter(d -> d.rl)
    ).apply(instance, DepartmentListing::new));

    private Department department() {
        return Shopaholic.ShopaholicRegistries.DEPARTMENTS.get(rl);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return department().getName();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Icon createIcon() {
        return department().getIcon();
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @Override
    public void purchase(Player player) {
        if (!player.level().isClientSide) {
            ShopTarget target = new ShopTarget(player.level(), player.blockPosition(), player, player, player.getMainHandItem(), new EntityShopInput(player));
            if (department() != null) {
                department().open(target, false);
            }
        }
    }

    @Override
    public String id() {
        return rl.toString();
    }

    @Override
    public boolean isValid() {
        return department() != null;
    }
}

