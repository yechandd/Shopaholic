package uk.joshiejack.shopaholic.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ShopaholicClientConfig {
    public static ModConfigSpec.BooleanValue enableShippingTicker;
    public static ModConfigSpec.BooleanValue enableClockHUD;
    public static ModConfigSpec.BooleanValue enableGoldHUD;
    public static ModConfigSpec.BooleanValue enableGoldIconHUD;
    public static ModConfigSpec.BooleanValue enableInventoryView;
    public static ModConfigSpec.IntValue goldHUDX;
    public static ModConfigSpec.IntValue goldHUDY;
    public static ModConfigSpec.EnumValue<GoldRenderSide> goldRenderSide;
    public static ModConfigSpec.BooleanValue enableSellValueTooltip;

    ShopaholicClientConfig(ModConfigSpec.Builder builder) {
        builder.push("General");
        enableInventoryView = builder.define("Enable inventory view in shops", true);
        enableShippingTicker = builder.define("Enable shipping daily ticker", true);
        enableSellValueTooltip = builder.define("Enable sell value in tooltips", false);
        builder.pop();
        builder.push("HUD");
        enableClockHUD = builder.define("Enable clock HUD", true);
        enableGoldHUD = builder.define("Enable gold HUD", true);
        enableGoldIconHUD = builder.define("Enable gold icon in HUD", true);
        goldHUDX = builder.defineInRange("Gold HUD X Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        goldHUDY = builder.defineInRange("Gold HUD Y Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        goldRenderSide = builder.defineEnum("Gold HUD render side", GoldRenderSide.LEFT);
        builder.pop();
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(ShopaholicClientConfig::new).getValue();
    }

    public enum GoldRenderSide {
        LEFT, RIGHT
    }
}
