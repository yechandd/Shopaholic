package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.network.shop.ServerPurchaseItemPacket;
import uk.joshiejack.shopaholic.world.shop.Listing;
import uk.joshiejack.shopaholic.world.shop.Sublisting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractListingButton extends AbstractButton<DepartmentScreen> {
    protected final Listing listing;
    protected final Sublisting sublisting;
    protected final Wallet wallet = Wallet.getActive();
    private boolean pressed;
    private int hoverTimer = 0;
    protected int stackSize = 0;
    protected boolean subTooltipHovered;

    public AbstractListingButton(DepartmentScreen screen, int x, int y, Listing listing) {
        super(screen, x, y, 200, 18, listing.getSubListing(screen.stock).getDisplayName(),
                (btn) -> PenguinNetwork.sendToServer(new ServerPurchaseItemPacket(screen.getMenu().department, listing,
                        ((AbstractListingButton) btn).stackSize)));

        this.sublisting = listing.getSubListing(screen.stock);
        this.listing = listing;
    }

    @Override
    public void onPress() {
        pressed = true;
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        int state = hovered && !canPurchase(1) ? 1 : hovered ? 2 : 0;
        //Background
        graphics.blit(screen.getMenu().shop.getExtra(), getX(), getY(), 0, state * 18, width / 2, height);
        graphics.blit(screen.getMenu().shop.getExtra(), getX() + width / 2, getY(), 200 - width / 2, state * 18, width / 2, height);
        //Foreground
        int color = !active ? 10526880 : hovered && canPurchase(1) ? 16777120 : 14737632;
        drawForeground(graphics, mouseX, mouseY, hovered, color);
        if (pressed)
            whilePressed();

        //Draw the sublisting tooltip
        if (subTooltipHovered) {
            List<Component> tooltip = new ArrayList<>();
            listing.getSubListing(screen.stock).addTooltip(tooltip);
            graphics.renderTooltip(screen.getMinecraft().font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.onPress.onPress(this);
        this.pressed = false;
        this.stackSize = 0;
        this.hoverTimer = 0;
    }

    private void whilePressed() {
        //If ctrl is down,
        if (hoverTimer == 0 || (hoverTimer % 10 == 0 && Screen.hasControlDown())) {
            Icon display = listing.getSubListing(screen.stock).getIcon();
            long goldCost = listing.getGoldCost(screen.getMenu().target.getPlayer(), screen.getMenu().department, screen.stock);
            int multiplier = Screen.hasShiftDown() ? 10 : 1;
            if (multiplier == 10 && !canPurchase(stackSize + 10)) multiplier = 1;
            if (canPurchase(multiplier)) {
                stackSize += multiplier;
                Wallet.getActive().setBalance(Wallet.getActive().getBalance() - goldCost * multiplier);
                screen.updatePurchased(display, multiplier * sublisting.getCount());
            }

            hoverTimer++;
        }

        if (Screen.hasControlDown()) {
            hoverTimer++;
        }
    }

    protected abstract void drawForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, int color);

    private boolean canPurchase(int x) {
        return wallet.getBalance() - (listing.getGoldCost(screen.getMenu().target.getPlayer(), screen.getMenu().department, screen.stock) * x) >= 0
                && listing.canPurchase(screen.getMenu().target.getPlayer(), screen.stock, x);
    }
}