package uk.joshiejack.shopaholic.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import uk.joshiejack.shopaholic.Shopaholic;

import java.util.Objects;

public class ShopaholicItemModels extends ItemModelProvider {
    public ShopaholicItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Shopaholic.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Shopaholic.ShopaholicItems.ITEMS.getEntries()
                .forEach(holder -> {
                    String path = Objects.requireNonNull(holder.getId()).getPath();
                    if (holder.get() instanceof BlockItem)
                        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
                    else
                        singleTexture(path, mcLoc("item/generated"), "layer0", modLoc("item/" + path));
                });
    }
}
