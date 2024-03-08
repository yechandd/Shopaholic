package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public abstract class NavigationButton extends AbstractButton<DepartmentScreen> {
    private final int textureX;

    public NavigationButton(DepartmentScreen screen, int x, int y, int scrollAmount, int textureX, Component name) {
        super(screen, x, y, 14, 12, name,
                (btn) -> screen.scroll(Screen.hasShiftDown() ? scrollAmount * 10 : scrollAmount));
        this.setTooltip(Tooltip.create(name));
        this.textureX = textureX;
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        updateVisibility();
        if (visible) {
            //mc.getTextureManager().bind(screen.getMenu().shop.getExtra()); //TODO: UP AND DOWN BUTTONS
           //TODO??? blit(matrix, x, y, textureX, (hovered ? 12 : 0), width, height);
        }
    }

    protected abstract void updateVisibility();
}