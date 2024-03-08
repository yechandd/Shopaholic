package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import uk.joshiejack.shopaholic.Shopaholic;

@SuppressWarnings("ConstantConditions")
public class ShopaholicBlockStates extends BlockStateProvider {
    public ShopaholicBlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Shopaholic.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
