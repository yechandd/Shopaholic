package uk.joshiejack.shopaholic.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import java.util.Set;

public class TrackingRenderer {
    private boolean loading;
    private final int ticker = 0;
    private int yOffset = 0;
    private long last;
    private final Set<Shipping.SoldItem> sold;

    public TrackingRenderer(Set<Shipping.SoldItem> sold) {
        this.sold = sold;
        this.loading = true;
    }

    private void renderAt(Minecraft mc, GuiGraphics graphics, Shipping.SoldItem stack, int x, int y) {
        graphics.renderItem(stack.getStack(), x, y - 18);
        graphics.blitSprite(RenderGold.SPRITE, x + 25, y - 16, 12, 12);
        String text = StringHelper.convertNumberToString(stack.getValue());
        graphics.drawString(mc.font, text, x + 35, y - 13, 0xFFFFFFFF);
//            mc.getItemRenderer().renderGuiItem(stack.getStack(), x, y - 18);
//            mc.getTextureManager().bind(DepartmentScreen.EXTRA);
//            mc.gui.blit(matrix, x + 21, y - 16, 244, 244, 12, 12);
//            String text = StringHelper.convertNumberToString(stack.getValue());
//            mc.font.drawShadow(matrix, text, x + 35, y - 13, 0xFFFFFFFF);
    }

    private boolean hasFinishedOrUpdateTickerUp() {
        long current = System.currentTimeMillis();
        if (current - last >= 30) {
            if (yOffset + 1 >= sold.size() * 20) {
                if (current - last >= 1500) {
                    last = System.currentTimeMillis();
                    return true;
                }
            } else {
                last = System.currentTimeMillis();
                yOffset++;
            }
        }

        return false;
    }

    private void moveItemsDown() {
        long current = System.currentTimeMillis();
        if (current - last >= 100) {
            if (yOffset <= 0) {
                NeoForge.EVENT_BUS.unregister(this);
            } else {
                last = System.currentTimeMillis();
                yOffset -= 2;
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            GuiGraphics graphics = event.getGuiGraphics();
            int maxHeight = event.getWindow().getGuiScaledHeight();
            Minecraft mc = Minecraft.getInstance();
            graphics.pose().pushPose();
            int y = 0;
            int currentY = maxHeight + (20 * sold.size()) - yOffset;
            for (Shipping.SoldItem stack : sold) {
                renderAt(mc, event.getGuiGraphics(), stack, 0, currentY - y);
                y += 20; //Increase the y
            }

            graphics.pose().popPose();
            if (loading && hasFinishedOrUpdateTickerUp()) loading = false;
            else if (!loading) moveItemsDown();
        }
    }
}
