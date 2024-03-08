package uk.joshiejack.shopaholic.client.renderer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.client.ShopaholicClientConfig;
import uk.joshiejack.shopaholic.world.shipping.ShippingRegistry;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Shopaholic.MODID, value = Dist.CLIENT)
public class SellValueTooltip {
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        if (ShopaholicClientConfig.enableSellValueTooltip.get() && ShippingRegistry.getValue(event.getItemStack()) > 0 && event.getFlags().isAdvanced()) //TODO: Render the gold instead?
            event.getToolTip().add(Component.nullToEmpty("Sell Value: " + ChatFormatting.GOLD + StringHelper.convertNumberToString(ShippingRegistry.getValue(event.getItemStack())) + " Gold"));
    }
}