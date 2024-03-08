package uk.joshiejack.shopaholic.client.gui.widget.label;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractLabel;
import uk.joshiejack.shopaholic.client.ShopaholicClient;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class ShippedItemLabel extends AbstractLabel<Book> {
    private final ItemStack icon;
    private final long value;

    public ShippedItemLabel(Book book, int x, int y, ItemStack stack, long value) {
        super(book, x, y, 16, 16, ShopaholicClient.EMPTY_STRING);
        this.icon = stack;
        this.value = value;
    }

    @Override
    public void renderLabel(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = getX();
        int y = getY();
        graphics.renderItem(icon, x, y);
        float scale = 0.75F;
        int xPos = (int) ((x + 3) / scale);
        int yPos = (int) ((y + 3) / scale);
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, scale);
        graphics.renderItemDecorations(screen.getMinecraft().font, icon, xPos, yPos);
        graphics.pose().popPose();
        boolean hovered = clicked(mouseX, mouseY);
        if (hovered) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(icon.getHoverName());
            tooltip.add(Component.translatable(NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "G").withStyle(ChatFormatting.GOLD));
            graphics.renderComponentTooltip(screen.getMinecraft().font, tooltip, mouseX, mouseY);
        }
    }
}