package uk.joshiejack.shopaholic.client.gui.widget.label;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.PenguinFonts;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractLabel;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.client.renderer.RenderGold;
import uk.joshiejack.shopaholic.client.shipping.Shipped;
import uk.joshiejack.shopaholic.world.shipping.Shipping;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class ShippingLogLabel extends AbstractLabel<Book> {
    private static final Component NAME = Component.translatable("gui.shopaholic.manager.shipping").withStyle(ChatFormatting.UNDERLINE);
    private static final Component COMBINED = Component.translatable("gui.shopaholic.manager.combined").withStyle(ChatFormatting.BOLD);
    private final long total;
    private final long overall;

    public ShippingLogLabel(Book gui, int x, int y) {
        super(gui, x, y, 0, 0, ShopaholicClient.EMPTY_STRING);
        long total = 0L;
        for (Shipping.SoldItem holderSold : Shipped.getShippingLog()) {
            total += holderSold.getValue();
        }

        this.total = total;
        this.overall = Wallet.getWallet(WalletType.PERSONAL).getIncome() + Wallet.getWallet(WalletType.SHARED).getIncome();
    }

    @Override
    public void renderLabel(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = getX();
        int y = getY();
        Minecraft mc = Minecraft.getInstance();
        //GlStateManager.disableDepth();
        //Draw the shipping log
        graphics.drawString(mc.font, NAME, (int)(x + 61 - (mc.font.width(NAME) / 2F)), y - 10, 0xFFFFFF);
        //mc.font.drawShadow(matrix, NAME, x + 61 - (mc.font.width(NAME) / 2F), y - 10, 0xFFFFFF);

        graphics.hLine( x - 6, x + 115, y, screen.lineColor1);
        graphics.hLine(x - 5, x + 116, y + 1, screen.lineColor2);

        //Draw the statistics
        //mc.font.drawShadow(graphics, COMBINED, x + 61 - (mc.font.width(COMBINED) / 2F), y + 128, book.fontColor1);
        graphics.drawString(PenguinFonts.UNICODE.get(), COMBINED, (int)(x + 61 - (mc.font.width(COMBINED) / 2F)), y + 128, screen.fontColor1);
        graphics.hLine( x - 6, x + 115, y + 138, screen.lineColor1);
        graphics.hLine( x - 5, x + 116, y + 139, screen.lineColor2);

        //Draw the shipping log totals
        if (total > 0L) {
            //GlStateManager.color(1F, 1F, 1F, 1F);
            graphics.blitSprite(RenderGold.SPRITE, x - 3 + 18, y + 110 + 4, 12, 12);
            String text = NumberFormat.getNumberInstance(Locale.ENGLISH).format(total);
            //mc.font.drawShadow(graphics, text, x - 3 + 32, y + 110 + 6, 0xFFFFFFFF);
            graphics.drawString(mc.font, text, x - 3 + 32, y + 110 + 6, 0xFFFFFFFF);
        }

        //mc.getTextureManager().bind(DepartmentScreen.EXTRA);
        graphics.blitSprite(RenderGold.SPRITE, x - 3 + 18, y + 138 + 4, 12, 12);
        String text = NumberFormat.getNumberInstance(Locale.ENGLISH).format(overall);
        //mc.font.drawShadow(graphics, text, x - 3 + 32, y + 138 + 6, 0xFFFFFFFF);
        graphics.drawString(mc.font, text, x - 3 + 32, y + 138 + 6, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput element) { }
}

