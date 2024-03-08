package uk.joshiejack.shopaholic.client.gui.widget.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.MultiTooltip;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.client.renderer.RenderGold;
import uk.joshiejack.shopaholic.network.bank.TransferBalancePacket;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TransferBalanceButton extends AbstractButton<Book> {
    private static final ResourceLocation DEFAULT = new ResourceLocation(Shopaholic.MODID, "transfer_button");
    private static final ResourceLocation HOVERED = new ResourceLocation(Shopaholic.MODID, "transfer_button_highlighted");
    private static final Component FROM_SHARED = Component.translatable("gui." + Shopaholic.MODID + ".manager.from.shared").withStyle(ChatFormatting.GOLD);
    private static final Component FROM_PERSONAL = Component.translatable("gui." + Shopaholic.MODID + ".manager.from.personal").withStyle(ChatFormatting.GOLD);
    private static final Component X10 = Component.translatable("gui." + Shopaholic.MODID + ".manager.x.10", Component.literal("SHIFT").withStyle(ChatFormatting.AQUA));
    private static final Component X100 = Component.translatable("gui." + Shopaholic.MODID + ".manager.x.100", Component.literal("CTRL").withStyle(ChatFormatting.GREEN));
    private static final Component X1000 = Component.translatable("gui." + Shopaholic.MODID + ".manager.x.1000", Component.literal("ALT").withStyle(ChatFormatting.LIGHT_PURPLE));
    private final long value;

    public TransferBalanceButton(WalletType from, long value, Book book, int x, int y) {
        super(book, x, y, 23, 10, Component.literal(String.valueOf(value)), (btn) -> {
            long gold = value;
            if (Screen.hasShiftDown()) gold *= 10;
            if (Screen.hasControlDown()) gold *= 100;
            if (Screen.hasAltDown()) gold *= 1000;
            PenguinNetwork.sendToServer(new TransferBalancePacket(from, gold));
        });
        setMultiTooltip(MultiTooltip.create((from == WalletType.SHARED ? FROM_SHARED : FROM_PERSONAL), X10, X100, X1000));
        this.value = value;
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        screen.bindLeftTexture();
        int x = getX();
        int y = getY();
        graphics.blitSprite(hovered ? HOVERED : DEFAULT, x, y,  23, 10);
        //blit(matrix, x, y, hovered ? 23 : 0, 193, 23, 10);
        //Texture drawn, now to draw the text
        graphics.pose().pushPose();
        float scale = 0.5F;
        graphics.pose().scale(scale, scale, scale);
        long gold = value;
        if (Screen.hasShiftDown()) gold *= 10;
        if (Screen.hasControlDown()) gold *= 100;
        if (Screen.hasAltDown()) gold *= 1000;
        graphics.drawString(mc.font, StringHelper.convertNumberToString(gold), (int)((x + 9) / scale), (int)((y + 3) / scale), 0xFFFFFF);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        //screen.minecraft().getTextureManager().bind(DepartmentScreen.EXTRA);
        graphics.blitSprite(RenderGold.SPRITE, (int)((x + 2) / scale), (int)((y + 2) / scale), 12, 12);
        //blit(matrix, (int)((x + 2) / scale), (int)((y + 2) / scale), 244, 244, 12, 12);
        graphics.pose().popPose();
    }
}