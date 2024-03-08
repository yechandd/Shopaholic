package uk.joshiejack.shopaholic.client.gui.widget.button;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
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
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.network.bank.SwitchWalletPacket;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SwitchAccountButton extends AbstractButton<Book> {
    public static final ResourceLocation TICK = new ResourceLocation(Shopaholic.MODID, "tick");
    public static final ResourceLocation TICK_HIGHLIGHTED = new ResourceLocation(Shopaholic.MODID, "tick_highlighted");
    public static final ResourceLocation CROSS = new ResourceLocation(Shopaholic.MODID, "cross");
    public static final ResourceLocation CROSS_HIGHLIGHTED = new ResourceLocation(Shopaholic.MODID, "cross_highlighted");
    private static final List<Component> active = Lists.newArrayList(Component.translatable("gui.shopaholic.manager.wallet.currently",
            Component.translatable("gui.shopaholic.manager.wallet.active").withStyle(ChatFormatting.GREEN)));
    private static final List<Component> inactive = Lists.newArrayList(Component.translatable("gui.shopaholic.manager.wallet.currently",
            Component.translatable("gui.shopaholic.manager.wallet.inactive").withStyle(ChatFormatting.RED)),
            Component.translatable("gui.shopaholic.manager.wallet.switch"));
    private final boolean isWalletActive;

    public SwitchAccountButton(WalletType type, Book book, int x, int y) {
        super(book, x, y, 7, 8, Component.empty(), (btn) -> {
            boolean isWalletActive = Wallet.getActive() == Wallet.getWallet(type);
            if (!isWalletActive)
                PenguinNetwork.sendToServer(new SwitchWalletPacket(Wallet.getActive() == Wallet.getWallet(WalletType.PERSONAL)));
        });
        this.isWalletActive = Wallet.getActive() == Wallet.getWallet(type);
        this.setMultiTooltip(MultiTooltip.create(isWalletActive ? active : inactive));
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        if (visible) {
            StringHelper.enableUnicode();
            screen.bindLeftTexture();
            //31 + (!isWalletActive ? 10 : 0) + (hovered ? 17 : 0), 248
            ResourceLocation sprite = isWalletActive ? (hovered ? TICK_HIGHLIGHTED : TICK) : (hovered ? CROSS_HIGHLIGHTED : CROSS);
            matrix.blitSprite(sprite, getX(), getY(), 7 + (isWalletActive ? 3 :0), 8);
            StringHelper.disableUnicode();
        }
    }
}
