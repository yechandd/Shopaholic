package uk.joshiejack.shopaholic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.tab.Tab;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.client.gui.DepartmentScreen;
import uk.joshiejack.shopaholic.client.gui.page.PageEconomyManager;
import uk.joshiejack.shopaholic.plugin.SimplySeasonsPlugin;
import uk.joshiejack.shopaholic.world.inventory.DepartmentMenu;
import uk.joshiejack.shopaholic.world.inventory.ShopaholicMenus;

@Mod.EventBusSubscriber(modid = Shopaholic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ShopaholicClient {
    public static final Component EMPTY_STRING = Component.empty();

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ShopaholicMenus.BOOK.get(),
                ((AbstractBookMenu container, Inventory inv, Component text) ->
                        Book.getInstance(Shopaholic.MODID, container, inv, text, (book) -> {
                            Component manager = Component.translatable("gui." + Shopaholic.MODID + ".manager");
                            book.withTab(new Tab(manager, PageEconomyManager.ICON)).withPage(new PageEconomyManager(manager));
                            //Setup colours
                            book.fontColor1 = 4210752;
                            book.fontColor2 = 0x3F3F3F;
                            book.lineColor1 = 0xFF515557;
                            book.lineColor2 = 0xFF7F8589;
                        })
                ));
        event.register(ShopaholicMenus.SHOP.get(),
                ((DepartmentMenu container, Inventory inv, Component text) -> new DepartmentScreen(container, inv)));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        boolean isSimplySeasonsEnabled =  ModList.get().isLoaded("simplyseasons") && SimplySeasonsPlugin.isHUDEnabled();
        if (ShopaholicClientConfig.enableClockHUD.get() && !isSimplySeasonsEnabled) {
            HUDRenderer.RENDERERS.put(Level.OVERWORLD, new HUDRenderer.HUDRenderData() {
                @Override
                public Component getHeader(Minecraft mc) {
                    return  Component.translatable("Day %s", 1 + TimeHelper.getElapsedDays(mc.level.getDayTime()));
                }

                @Override
                public int getX() {
                    return -20;
                }
            });
        }
    }

    public static void refreshShop() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof DepartmentScreen) {
            ((DepartmentScreen)screen).refresh();
        }
    }
}