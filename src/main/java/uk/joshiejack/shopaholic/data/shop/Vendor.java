package uk.joshiejack.shopaholic.data.shop;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.shopaholic.world.shop.Department;

public class Vendor {
    protected static final Vendor NONE = new Vendor(Department.ShopTargetType.COMMAND, "");
    protected final Department.ShopTargetType type;
    protected final String data;

    public Vendor(Department.ShopTargetType type, String data) {
        this.type = type;
        this.data = data;
    }

    public static Vendor entity(EntityType<?> vendor) {
        return new Vendor(Department.ShopTargetType.ENTITY, RegistryHelper.id(vendor).toString());
    }

    public static Vendor item(Item item) {
        return new Vendor(Department.ShopTargetType.ITEM ,RegistryHelper.id(item).toString());
    }

    public static Vendor block(Block block) {
        return new Vendor(Department.ShopTargetType.BLOCK, RegistryHelper.id(block).toString());
    }

    public static Vendor command(String command) {
        return new Vendor(Department.ShopTargetType.COMMAND, command);
    }
}
