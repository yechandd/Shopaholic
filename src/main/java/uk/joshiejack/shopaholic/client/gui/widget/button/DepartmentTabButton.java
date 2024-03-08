package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.network.shop.SwitchDepartmentPacket;
import uk.joshiejack.shopaholic.world.shop.Department;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DepartmentTabButton extends AbstractButton<DepartmentScreen> {
    public static final ResourceLocation STANDARD = new ResourceLocation(Shopaholic.MODID, "tab");
    public static final ResourceLocation HIGHLIGHTED = new ResourceLocation(Shopaholic.MODID, "tab_highlighted");
    private final Department department;

    public DepartmentTabButton(DepartmentScreen screen, int x, int y, Department department) {
        super(screen, x, y, 21, 21, ShopaholicClient.EMPTY_STRING,
                (btn) -> {
                    //Shop.get(department).setLast(department);
                    PenguinNetwork.sendToServer(new SwitchDepartmentPacket(department));
                });
        this.setTooltip(Tooltip.create(department.getName()));
        this.department = department;
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        //mc.getTextureManager().bind(screen.getMenu().shop.getExtra());
        graphics.blitSprite(hovered ? HIGHLIGHTED : STANDARD, getX(), getY(), width, height);
        //blit(graphics, x, y, 107, 59 + (hovered ? 22 : 0), 21, 22);
        department.getIcon().render(mc, graphics, getX() + 4, getY() + 3);
    }
}