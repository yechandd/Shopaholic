package uk.joshiejack.shopaholic.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.client.ShopaholicClientConfig;
import uk.joshiejack.shopaholic.client.bank.Wallet;

import java.text.NumberFormat;
import java.util.Locale;

import static uk.joshiejack.shopaholic.Shopaholic.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RenderGold {
    public static final ResourceLocation SPRITE = new ResourceLocation(MODID, "gold");

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Register {
        @SubscribeEvent
        public static void registerGuiOverlay(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll(new ResourceLocation(Shopaholic.MODID, "gold"), (gui, graphics, partialTick, width, height) -> {
                if(!ShopaholicClientConfig.enableGoldHUD.get()) return;
                if (!gui.getMinecraft().options.hideGui) {
                    gui.setupOverlayRenderState(true, false);
                    graphics.pose().pushPose();
                    renderGold(gui.getMinecraft(), graphics);
                    graphics.pose().popPose();
                }
            });
        }
    }

    @SubscribeEvent //TODO: Re-enable maybe?
    public static void offsetPotion(RenderGuiOverlayEvent.Pre event) {
//        if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
//            event.getMatrixStack().pushPose();
//            event.getMatrixStack().translate(0F, 16F, 0F);
//        }
    }

    @SubscribeEvent
    public static void offsetPotion(RenderGuiOverlayEvent.Post event) {
//        if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
//            event.getMatrixStack().popPose();
//        }
    }

    private static void renderGold(Minecraft mc, GuiGraphics graphics) {
        RenderSystem.enableBlend();
        int maxWidth = mc.getWindow().getGuiScaledWidth();
        int maxHeight = mc.getWindow().getGuiScaledHeight();
        String text = NumberFormat.getNumberInstance(Locale.ENGLISH).format(Wallet.getActive().getBalance());
        float adjustedX = ((ShopaholicClientConfig.goldHUDX.get() / 100F) * maxWidth);
        float adjustedY = ((ShopaholicClientConfig.goldHUDY.get() / 100F) * maxHeight);

        if (ShopaholicClientConfig.enableGoldIconHUD.get()) {
            int coinX = (int) (ShopaholicClientConfig.goldRenderSide.get() == ShopaholicClientConfig.GoldRenderSide.LEFT ? maxWidth - mc.font.width(text) - 20 + adjustedX : maxWidth - adjustedX - 14);
            graphics.blitSprite(SPRITE, coinX, (int) (2 + adjustedY), 12, 12);
        }

        int textX = (int) (ShopaholicClientConfig.goldRenderSide.get() == ShopaholicClientConfig.GoldRenderSide.LEFT ? maxWidth - mc.font.width(text) - 5 + (int) adjustedX : maxWidth - adjustedX - 18 - mc.font.width(text));
        graphics.drawString(mc.font, text, textX, (int) (4 + adjustedY), 0xFFFFFFFF);
        RenderSystem.disableBlend();
    }
}