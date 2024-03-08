package uk.joshiejack.shopaholic.world.shop.input;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.shopaholic.api.shop.ShopInput;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;

public class BlockShopInput extends ShopInput<Block> {
    public static int ID = 0;

    public BlockShopInput(Block block) {
        super(BuiltInRegistries.BLOCK, block);
    }

    public BlockShopInput(FriendlyByteBuf buf) {
        super(BuiltInRegistries.BLOCK, buf);
    }

    @Override
    public String getName(ShopTarget target) {
        BlockEntity tile = target.getLevel().getBlockEntity(target.getPos());
        return !(tile instanceof Nameable) ? super.getName(target) : ((Nameable)tile).getDisplayName().getString();
    }

    @Override
    public int intID() {
        return ID;
    }
}