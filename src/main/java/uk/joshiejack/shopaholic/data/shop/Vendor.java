package uk.joshiejack.shopaholic.data.shop;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.shopaholic.world.shop.Department;

public class Vendor {
    protected static final Vendor NONE = new Vendor("none", Department.ShopTargetType.COMMAND, "");
    protected final String id;
    protected final Department.ShopTargetType type;
    protected final String data;

    public Vendor(String id, Department.ShopTargetType type, String data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public static Vendor entity(String id, EntityType<?> vendor) {
        return new Vendor(id, Department.ShopTargetType.ENTITY, RegistryHelper.id(vendor).toString());
    }

    public static Vendor item(String id, Item item) {
        return new Vendor(id, Department.ShopTargetType.ITEM ,RegistryHelper.id(item).toString());
    }

    public static Vendor block(String id, Block block) {
        return new Vendor(id, Department.ShopTargetType.BLOCK, RegistryHelper.id(block).toString());
    }

    public static Vendor command(String id, String command) {
        return new Vendor(id, Department.ShopTargetType.COMMAND, command);
    }
}
