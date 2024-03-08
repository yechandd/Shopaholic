package uk.joshiejack.shopaholic.client.gui;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.AbstractContainerScreen;
import uk.joshiejack.penguinlib.client.gui.PenguinFonts;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.client.ShopaholicClientConfig;
import uk.joshiejack.shopaholic.client.bank.Wallet;
import uk.joshiejack.shopaholic.client.gui.widget.button.*;
import uk.joshiejack.shopaholic.client.renderer.RenderGold;
import uk.joshiejack.shopaholic.world.inventory.DepartmentMenu;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;
import uk.joshiejack.shopaholic.world.shop.Shop;
import uk.joshiejack.shopaholic.world.shop.inventory.Stock;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ConstantConditions")
public class DepartmentScreen extends AbstractContainerScreen<DepartmentMenu> {
    public static final ResourceLocation INVENTORY = new ResourceLocation(Shopaholic.MODID, "inventory");
    private static final DecimalFormat formatter = new DecimalFormat("#,###");
    private static final Component FREE = Component.translatable("gui.shopaholic.shop.free");
    private static final Component ERROR = Component.translatable("gui.shopaholic.shop.error");
    private static final Cache<Long, Component> COST_CACHE = CacheBuilder.newBuilder().build();
    private static final Object2IntMap<Department> SCROLL_POS = new Object2IntOpenHashMap<>();
    private static Shop prevShop;
    private static Double prevMouseX;
    private static Double prevMouseY;
    public Stock stock;
    private Pair<Icon, Integer> purchased;
    private int listingCount;
    private int end;

    public DepartmentScreen(DepartmentMenu container, Inventory inv) {
        super(container, inv, container.department.getName(), container.department.getShop().getBackground(), 256, 256);
    }

    @Override
    protected void initScreen(@NotNull Minecraft minecraft, @NotNull Player player) {
        setStart(getStartPos()); //Reload the screen and reset the mouse position
        if (prevMouseX != null && prevMouseY != null && menu.shop == prevShop) {
            minecraft.mouseHandler.xpos = prevMouseX;
            minecraft.mouseHandler.ypos = prevMouseY;
            InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(), 212993, minecraft.mouseHandler.xpos, minecraft.mouseHandler.ypos);
            prevMouseX = null;
            prevMouseY = null;
            prevShop = null;
        }
    }

    private Component getShopName() {
        return menu.shop != null ? menu.shop.getLocalizedName() : Component.empty(); //TODO? unknown shop
    }

    @Override
    public void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        //TODO: huh?renderBackground(graphics);
        //minecraft.getTextureManager().bind(background);
        int heightToUse = Math.max(listingCount, 3);
        if (heightToUse < 12) {
            graphics.blit(texture, leftPos, topPos - 12 + (20 * heightToUse), 0, 228, imageWidth, 28);
            graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, (20 * heightToUse) - 2);
        } else graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics matrix, int mouseX, int mouseY) {
        matrix.pose().pushPose();
        int width = PenguinFonts.FANCY.get().width(getShopName());
        boolean larger = width <= 90;
        boolean smaller = width >= 140;
        float scale = larger ? 1.5F : smaller ? 0.75F : 1F;
        matrix.pose().scale(scale, scale, scale);
        matrix.drawString(PenguinFonts.FANCY.get(), getShopName(), (int) (22 / scale), (int)((17 / scale) + (!larger && !smaller ? 3 : smaller ? 6 : 0)), 0xF1B81F);
        matrix.pose().popPose();
        drawCoinage(matrix, Wallet.getActive().getBalance());
        drawPlayerInventory(matrix);
        if (purchased.getLeft() != ItemIcon.EMPTY) {
            //TODO: Redraw the purchased items???
            //float blit = minecraft.getItemRenderer().blitOffset;
            //minecraft.getItemRenderer().blitOffset = 100F;
            //int blit2 = minecraft.gui.getBlitOffset();
            //minecraft.gui.setBlitOffset(110);
            purchased.getLeft().setCount(purchased.getRight())
                    .render(minecraft, matrix, mouseX - leftPos - 8, mouseY - topPos - 8);
            //purchased.getLeft().renderWithCount(minecraft, matrix, mouseX - leftPos - 8, mouseY - topPos - 8, purchased.getRight());
            //minecraft.getItemRenderer().blitOffset = blit;
            //minecraft.gui.setBlitOffset(blit2);
        }
    }

    private void drawCoinage(GuiGraphics graphics, long gold) {
        String formatted = formatter.format(gold);
        int width = PenguinFonts.FANCY.get().width(formatted);
        graphics.drawString(PenguinFonts.FANCY.get(), formatted, 220 - width, 20, 0xFFF5CB5C);
        //PenguinFonts.FANCY.get().draw(matrix, formatted, 220 - width, 20, 0xFFF5CB5C);
        //minecraft.getTextureManager().bind(EXTRA);
        graphics.blitSprite(RenderGold.SPRITE, 224, 17, 12, 12);
    }

    private void drawPlayerInventory(GuiGraphics graphics) {
        if (!ShopaholicClientConfig.enableInventoryView.get()) return;
        graphics.blitSprite(INVENTORY, 240, 40, 100, 194);
        graphics.drawString(PenguinFonts.FANCY.get(), "Inventory", 260, 44, 0xFFF5CB5C);
    }

    public void refresh() {
        setStart(getStartPos());
    }

    private int getStartPos() {
        return SCROLL_POS.getOrDefault(menu.department, 0);
    }

    public void setStart(int i) {
        clearWidgets();

        purchased = Pair.of(ItemIcon.EMPTY, 0);
        stock = menu.department.getStockLevels(minecraft.level);
        Collection<Listing> contents = Lists.newArrayList();
        for (Listing listing : menu.department.getListings()) {
            if (listing.canList(menu.target, menu.department, stock)) {
                contents.add(listing);
            }
        }

        //Up Arrow
        addRenderableWidget(new NavigationButton(this, leftPos + 232, topPos + 60, -1, 225, Component.translatable("button.penguinlib.previous")) {
            @Override
            protected void updateVisibility() {
                visible = getStartPos() != 0;
            }
        });

        //Down Arrow
        addRenderableWidget(new NavigationButton(this, leftPos + 232, topPos + 210, +1, 242, Component.translatable("button.penguinlib.next")) {
            @Override
            protected void updateVisibility() {
                visible = getStartPos() < end;
            }
        });

        end = contents.size() - 10;
        SCROLL_POS.put(menu.department, Math.max(0, Math.min(end, i)));
        listingCount = 2;

        //Arrows are added, now add the items being sold
        int position = 0;
        int pPosition = 0;
        int start = getStartPos();
        Iterator<Listing> it = contents.iterator();
        while (it.hasNext() && position <= 180) {
            Listing listing = it.next();
            if (pPosition >= start && listing.canList(menu.target, menu.department, stock)) {
                if (listing.getGoldCost(menu.target.getPlayer(), menu.department, stock) < 0) {
                    addRenderableWidget(new ItemListingButton(this, leftPos + 28, 38 + topPos + position, listing));
                    listingCount++;
                    position += 20;
                } else {
                    int add = addButton(listing, leftPos + 28, 38 + topPos + position, position);
                    listingCount = add > 0 ? listingCount + 1 : listingCount;
                    position += add;
                }
            }

            pPosition++;
        }

        if (listingCount == 2) addRenderableOnly(new OutOfStockLabel(minecraft.font, leftPos + 28, 38 + topPos + position, menu.department));
        //Tabs
        if (menu.shop != null && menu.shop.getDepartments().size() > 1) {
            int j = 0;
            for (Department department : menu.shop.getDepartments()) {
                if (department.isValidFor(menu.target, Condition.CheckType.SHOP_LISTING)) {
                    if (department.getListings().stream().anyMatch(l -> l.canList(menu.target, menu.department, stock))) {
                        addRenderableWidget(new DepartmentTabButton(this, leftPos + 5, topPos + 38 + (j * 23), department));
                        j++;
                    }
                }
            }

            if (listingCount < (3 + menu.shop.getDepartments().size())) {
                listingCount = 3 + menu.shop.getDepartments().size();
            }
        }

        //addButton(new DropItemButton(this));
    }

    private int addButton(Listing listing, int left, int top, int space) {
        if (listing.getSubListing(stock).isGoldOnly()) {
            if (space + 20 <= 200) {
                addRenderableWidget(new GoldListingButton(this, left, top, listing));
                return 20;
            }
        } else {
            if (listing.getSubListing(stock).getMaterials().size() == 1 && listing.getGoldCost(menu.target.getPlayer(), menu.department, stock) == 0) {
                if (space + 20 <= 200) {
                    addRenderableWidget(new ItemListingButton(this, left, top, listing));
                    return 20;
                }
            } else if (space + 20 <= 200) {
                addRenderableWidget(new ComboListingButton(this, left, top, listing));
                return 20;
            }
        }

        return 0;
    }

    public static Component getCostAsTextComponent(long cost) {
        try {
            return cost == 0 ? FREE :
                    COST_CACHE.get(cost, () -> Component.literal(StringHelper.convertNumberToString(cost)));
        } catch (ExecutionException ex) {
            return ERROR;
        }
    }

    public void updatePurchased(@Nonnull Icon icon, int amount) {
        if (icon == ItemIcon.EMPTY) purchased = Pair.of(ItemIcon.EMPTY, 0);
        else if (purchased.getLeft() == ItemIcon.EMPTY || purchased.getLeft() != icon)
            purchased = Pair.of(icon, amount);
        else purchased = Pair.of(purchased.getLeft(), purchased.getRight() + amount);
    }

    @Override
    public boolean mouseClicked(double x, double y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        prevMouseX = minecraft.mouseHandler.xpos;
        prevMouseY = minecraft.mouseHandler.ypos;
        prevShop = menu.shop;
        //Skip the container below
        for (GuiEventListener iguieventlistener : this.children()) {
            if (iguieventlistener.mouseClicked(x, y, mouseButton)) {
                setFocused(iguieventlistener);
                if (mouseButton == 0) {
                    this.setDragging(true);
                }

                return true;
            }
        }

        updatePurchased(ItemIcon.EMPTY, 0);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dir, double huh) {
        scroll((int) -dir);
        return super.mouseScrolled(mouseX, mouseY, dir, huh);
    }

    public void scroll(int amount) {
        setStart(getStartPos() + amount);
        init(); //reload?
    }

    @Override
    public int getXSize() {
        return 512;
    }
}