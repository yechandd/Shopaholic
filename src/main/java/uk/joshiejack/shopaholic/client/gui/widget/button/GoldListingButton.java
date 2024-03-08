package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.client.renderer.RenderGold;
import uk.joshiejack.shopaholic.world.shop.Listing;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class GoldListingButton extends AbstractListingButton {
    public GoldListingButton(DepartmentScreen screen, int x, int y, Listing listing) {
        super(screen, x, y, listing);
    }

    @Override
    protected void drawForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, int color) {
        sublisting.getIcon().render(mc, graphics, getX() + 2, getY() + 1);
        graphics.drawString(mc.font, getMessage(), getX() + 20, getY() + (height - 8) / 2, color);
        //GlStateManager.color(1.0F, 1.0F, 1.0F);
        //Draw the cost
        long goldCost = listing.getGoldCost(screen.getMenu().target.getPlayer(), screen.getMenu().department, screen.stock);
        Component cost = DepartmentScreen.getCostAsTextComponent(goldCost);
        if (goldCost > 0) {
            int width = mc.font.width(cost);
            graphics.drawString(mc.font, cost, getX() + 180 - width, getY() + (height - 8) / 2, color);
            //drawString(matrix, mc.font, cost, x + 180 - width, y + (height - 8) / 2, color);
            graphics.blitSprite(RenderGold.SPRITE, getX() + 184, getY() + (height - 8) / 2, 12, 12);
            //mc.getTextureManager().bind(DepartmentScreen.EXTRA);
            //blit(matrix, x + 184, (y + (height - 8) / 2) - 2, 244, 244, 12, 12);
        } else if (goldCost == 0 && sublisting.getMaterials().isEmpty()) {
            int width = mc.font.width(cost);
            graphics.drawString(mc.font, cost, getX() + 194 - width, getY() + (height - 8) / 2, color);
            //drawString(matrix, mc.font, cost, x + 194 - width, y + (height - 8) / 2, color);
        }
    }
}