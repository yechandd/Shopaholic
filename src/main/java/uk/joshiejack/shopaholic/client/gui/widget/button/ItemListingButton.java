package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.world.shop.Listing;
import uk.joshiejack.shopaholic.world.shop.MaterialCost;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ItemListingButton extends AbstractListingButton {
    private final MaterialCost requirement;
    private final Icon icon;

    public ItemListingButton(DepartmentScreen screen, int x, int y, Listing listing) {
        super(screen, x, y, listing);
        this.requirement = listing.getSubListing(screen.stock).getMaterials().get(0);
        this.icon = requirement.getIcon();
    }

    @Override
    protected void drawForeground(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, boolean hovered, int color) {
        int x = getX();
        int y = getY();
        sublisting.getIcon().render(mc, matrix, x + 2, y + 1);
        matrix.drawString(mc.font, getMessage(), x + 20, y + (height - 8) / 2, color);
        icon.setCount(requirement.getCost()).render(mc, matrix, x + 180, y + 1);
        boolean subTooltipHovered = mouseX >= x + 178 && mouseY >= y && mouseX < x + 198 && mouseY < y + 16;
        if (subTooltipHovered)
            matrix.renderComponentTooltip(screen.getMinecraft().font, requirement.getIcon().getTooltipLines(mc.player), mouseX, mouseY);
        if (subTooltipHovered)
            this.subTooltipHovered = true;
    }
}