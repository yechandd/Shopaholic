package uk.joshiejack.shopaholic.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.ShopaholicAPI;
import uk.joshiejack.shopaholic.api.shop.ListingType;

@Mixin(ShopaholicAPI.class)
public class ShopaholicApiInitializer {
    /**
     * @author joshiejack
     * @reason initialize the api
     */

    @Inject(method = "listingTypeRegistry()Lnet/minecraft/core/Registry;", at = @At("RETURN"), cancellable = true)
    private static void redirectListingTypeRegistry(CallbackInfoReturnable<Registry<Codec<? extends ListingType>>> cir) {
        cir.setReturnValue(Shopaholic.ShopaholicRegistries.LISTING_TYPE);
    }
}
