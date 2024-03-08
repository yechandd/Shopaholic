package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Shop;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class OutOfStockLabel extends AbstractStringWidget {
    private final Shop shop;

    public OutOfStockLabel(Font font, int x, int y, Department department) {
        super(x, y, 200, 18, department.getOutOfStockText(), font);
        this.shop = department.getShop();
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        //mc.getTextureManager().bind(shop.getExtra());
        int x = getX();
        int y = getY();
        //blit(graphics, x, y, 0, 0, width / 2, height); //TODO? What are we drawining here???
        //blit(graphics, x + width / 2, y, 200 - width / 2, 0, width / 2, height);
        graphics.drawString(mc.font, getMessage(), x + 10, y + (height - 8) / 2, 14737632);
    }
}