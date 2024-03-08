package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;
import uk.joshiejack.shopaholic.Shopaholic;

public class ShopaholicLanguage extends LanguageProvider {
    public ShopaholicLanguage(PackOutput gen) {
        super(gen, Shopaholic.MODID, "en_us");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addTranslations() {
        add("command.shopaholic.ship.success", "You successfully shipped %s");
        add("command.shopaholic.ship.success_many", "You successfully shipped %s items and failed to ship %s items");
        add("command.shopaholic.ship.no_value", "The item you tried to ship had no days");
        add("gui.shopaholic.shop.heal", "Health Restoration");
        add("gui.shopaholic.shop.free", "Free");
        add("gui.shopaholic.shop.error", "Error");
        add("gui.shopaholic.shop.outofstock", "Out of Stock");
        add("gui.shopaholic.manager", "Economy Manager");
        addManager("from.shared", "Click to transfer from the shared wallet to your personal wallet.");
        addManager("from.personal", "Click to transfer from your personal wallet to the shared wallet.");
        addManager("x.10", "Hold down %s for 10x");
        addManager("x.100", "Hold down %s for 100x");
        addManager("x.1000", "Hold down %s for 1000x");
        addManager("wallet.currently", "This wallet is currently %s");
        addManager("wallet.active", "active");
        addManager("wallet.inactive", "inactive");
        addManager("wallet.switch", "Click to switch to this wallet");
        addManager("transfer", "Transfer");
        addManager("expenses", "Expenses");
        addManager("income", "Income");
        addManager("profit", "Profit");
        addManager("balance", "Balance");
        addManager("account", "%s's account");
        addManager("name", "Account Manager");
        addManager("shipping", "Shipping Log");
        addManager("combined", "Combined Earnings");

        Shopaholic.ShopaholicItems.ITEMS.getEntries()
                .forEach(item -> add(item.get(), WordUtils.capitalizeFully(item.getId().getPath().replace("_", " "))));
    }

    private void addManager(String name, String text) {
        add("gui.shopaholic.manager." + name, text);
    }
}
