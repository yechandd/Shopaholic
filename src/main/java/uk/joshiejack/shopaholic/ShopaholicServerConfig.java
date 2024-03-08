package uk.joshiejack.shopaholic;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ShopaholicServerConfig {
    public static ModConfigSpec.LongValue minGold;
    public static ModConfigSpec.LongValue maxGold;
    public static ModConfigSpec.BooleanValue shipCommandEnabled;

    ShopaholicServerConfig(ModConfigSpec.Builder builder) {
        minGold = builder.defineInRange("Minimum gold", 0L, Long.MIN_VALUE, Long.MAX_VALUE);
        maxGold = builder.defineInRange("Maximum gold", Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE);
        shipCommandEnabled = builder.define("Enable '/shopaholic ship' command", true);
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(ShopaholicServerConfig::new).getValue();
    }
}