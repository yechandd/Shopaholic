package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.world.shop.Listing;
import uk.joshiejack.shopaholic.world.shop.MaterialCost;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ComboListingButton extends GoldListingButton {
    private final List<MaterialCost> icons;
    private static final float SCALE = 0.75F;

    public ComboListingButton(DepartmentScreen screen, int x, int y, Listing listing) {
        super(screen, x, y, listing);
        this.icons = sublisting.getMaterials();
    }

    @Override
    protected void drawForeground(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, boolean hovered, int color) {
        super.drawForeground(matrix, mouseX, mouseY, hovered, color);
        subTooltipHovered = false;
        for (int i = 0; i < icons.size(); i++) {
            MaterialCost material = icons.get(i);
            int x = this.getX() + (sublisting.getGold() == 0 ? 48 : 0) + 135 - (i * 16);
            int y = this.getY() + 3;
            boolean subTooltipHovered = mouseX >= x && mouseY >= y && mouseX < x + 14 && mouseY < y + 14;
            if (subTooltipHovered)
                matrix.renderComponentTooltip(screen.getMinecraft().font, material.getIcon().getTooltipLines(mc.player), mouseX, mouseY);
            if (subTooltipHovered)
                this.subTooltipHovered = true;
            matrix.pose().pushPose();
            matrix.pose().scale(SCALE, SCALE, SCALE);
            material.getIcon().setCount(material.getCost()).render(mc, matrix, (int) (x / SCALE), (int) (y / SCALE));
            matrix.pose().popPose();
//            RenderSystem.pushMatrix();
//            RenderSystem.scalef(SCALE, SCALE, SCALE);
//            material.getIcon().renderWithCount(mc, matrix, (int) (x / SCALE), (int) (y / SCALE), material.getCost());
//            RenderSystem.popMatrix();
        }
    }
}