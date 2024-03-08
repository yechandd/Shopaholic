package uk.joshiejack.shopaholic.client.gui.widget.label;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.PenguinFonts;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractLabel;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.client.ShopaholicClient;
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.client.renderer.RenderGold;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class EconomyStatsLabel extends AbstractLabel<Book> {
    private static final Component TRANSFER = Component.translatable("gui.shopaholic.manager.transfer");
    private static final Component EXPENSES = Component.translatable("gui.shopaholic.manager.expenses");
    private static final Component INCOME = Component.translatable("gui.shopaholic.manager.income");
    private static final Component PROFIT = Component.translatable("gui.shopaholic.manager.profit");
    private static final Component BALANCE = Component.translatable("gui.shopaholic.manager.balance");
    private static final Component NAME = Component.translatable("gui.shopaholic.manager.name").withStyle(ChatFormatting.UNDERLINE);

    private final Wallet personal;
    private final Wallet shared;
    private final String playerName;
    private final String teamName;

    public EconomyStatsLabel(Book b, int x, int y) {
        super(b, x, y, 0, 0, ShopaholicClient.EMPTY_STRING);
        Player player = Minecraft.getInstance().player;
        assert player != null;
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        personal = Wallet.getWallet(WalletType.PERSONAL);
        shared = player.getUUID().equals(team.getID()) ? null : Wallet.getWallet(WalletType.SHARED);
        playerName = WalletType.PERSONAL.getName(player);
        teamName = WalletType.SHARED.getName(player);
        System.out.println("ECONOMY STATS LABEL CREATED");
    }

    private static String formatGold(long value) {
        return NumberFormat.getNumberInstance(Locale.ENGLISH).format(value);
    }

    @Override
    public void renderLabel(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        System.out.println("RENDERING ECONOMY STATS LABEL");
        int x = getX();
        int y = getY();
        graphics.drawString(mc.font, NAME, (int) (x + 57 - (mc.font.width(NAME) / 2F)), y - 10, 0xFFFFFF);
        //Draw your personal balance
        //mc.font.drawShadow(graphics, ChatFormatting.BOLD + playerName, x, y, book.fontColor1);
        graphics.drawString(PenguinFonts.UNICODE.get(), ChatFormatting.BOLD + playerName, x, y + 50, screen.fontColor1);
        if (shared != null) {
            //mc.font.drawShadow(graphics, ChatFormatting.BOLD + teamName, x, y + 100, book.fontColor1);
            graphics.drawString(mc.font, ChatFormatting.BOLD + teamName, x, y + 100, screen.fontColor1);
            graphics.hLine( x - 6, x + 115, y + 46, screen.lineColor1);
            graphics.hLine( x - 5, x + 116, y + 46 + 1, screen.lineColor2);
            graphics.hLine( x - 6, x + 115, y + 96, screen.lineColor1);
            graphics.hLine( x - 5, x + 116, y + 96 + 1, screen.lineColor2);
            //mc.font.draw(graphics, ChatFormatting.BOLD + "^", x + 55, y + 64, book.fontColor1);
            graphics.drawString(mc.font, ChatFormatting.BOLD + "^", x + 55, y + 64, screen.fontColor1);
            int length = mc.font.width(TRANSFER);
            //mc.font.drawShadow(graphics, TRANSFER, x + 60 - length / 2F, y + 67, 0xFFFFFF);
            graphics.drawString(mc.font, TRANSFER, (int)(x + 60 - length / 2F), y + 67, 0xFFFFFF);
            //mc.font.draw(graphics, ChatFormatting.BOLD + "v", x + 55, y + 74, book.fontColor1);
            graphics.drawString(mc.font, ChatFormatting.BOLD + "v", x + 55, y + 74, screen.fontColor1);
        }

        graphics.pose().pushPose();
        float scale = 0.6666666666F;
        //graphics.scale(scale, scale, scale);
        graphics.pose().scale(scale, scale, scale);
        int xPos = (int) ((x + 45) / scale);
        ////Personal Account
        //Expenses
        graphics.drawString(mc.font, EXPENSES, (int) ((x) / scale), (int) ((y + 10) / scale), 0xFFFFFFFF);
        graphics.drawString(mc.font, formatGold(personal.getExpenses()), xPos, (int) ((y + 10) / scale), 0xFFFFFFFF);
        //Income
        graphics.drawString(mc.font, INCOME, (int) ((x) / scale), (int) ((y + 19) / scale), 0xFFFFFFFF);
        graphics.drawString(mc.font, formatGold(personal.getIncome()), xPos, (int) ((y + 19) / scale), 0xFFFFFFFF);
        //Profit
        graphics.drawString(mc.font, PROFIT, (int) ((x) / scale), (int) ((y + 28) / scale), 0xFFFFFFFF);
        graphics.drawString(mc.font, formatGold(personal.getIncome() - personal.getExpenses()), xPos, (int) ((y + 28) / scale), 0xFFFFFFFF);
        //Balance
        graphics.drawString(mc.font, BALANCE, (int) ((x) / scale), (int) ((y + 37) / scale), 0xFFFFFFFF);
        graphics.drawString(mc.font, formatGold(personal.getBalance()), xPos, (int) ((y + 37) / scale), 0xFFFFFFFF);

        if (shared != null) {
            ////Shared Account
            //Expenses
            graphics.drawString(mc.font, EXPENSES, (int) ((x) / scale), (int) ((y + 110) / scale), 0xFFFFFFFF);
            graphics.drawString(mc.font, formatGold(shared.getExpenses()), xPos, (int) ((y + 110) / scale), 0xFFFFFFFF);
            //Income
            graphics.drawString(mc.font, INCOME, (int) ((x) / scale), (int) ((y + 119) / scale), 0xFFFFFFFF);
            graphics.drawString(mc.font, formatGold(shared.getIncome()), xPos, (int) ((y + 119) / scale), 0xFFFFFFFF);
            //Profit
            graphics.drawString(mc.font, PROFIT, (int) ((x) / scale), (int) ((y + 128) / scale), 0xFFFFFFFF);
            graphics.drawString(mc.font, formatGold(shared.getIncome() - shared.getExpenses()), xPos, (int) ((y + 128) / scale), 0xFFFFFFFF);
            //Balance
            graphics.drawString(mc.font, BALANCE, (int) ((x) / scale), (int) ((y + 137) / scale), 0xFFFFFFFF);
            graphics.drawString(mc.font, formatGold(shared.getBalance()), xPos, (int) ((y + 137) / scale), 0xFFFFFFFF);
        }

        //noinspection
        //graphics.setColor(1F, 1F, 1F, 1F);
        //RenderSystem.color4f(1F, 1F, 1F, 1F);
        int goldX = (int) ((x + 35) / scale);
        //mc.getTextureManager().bind(DepartmentScreen.EXTRA);
        graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 9) / scale), 12, 12);
        graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 18) / scale), 12, 12);
        graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 27) / scale), 12, 12);
        graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 36) / scale), 12, 12);
        if (shared != null) {
            graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 109) / scale), 12, 12);
            graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 118) / scale), 12, 12);
            graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 127) / scale), 12, 12);
            graphics.blitSprite(RenderGold.SPRITE, goldX, (int) ((y + 136) / scale), 12, 12);
        }

        graphics.pose().popPose();
    }
}

