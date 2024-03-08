package uk.joshiejack.shopaholic.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.shopaholic.Shopaholic;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.api.shop.ShopInput;
import uk.joshiejack.shopaholic.api.shop.ShopTarget;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Shop;
import uk.joshiejack.shopaholic.world.shop.input.BlockShopInput;
import uk.joshiejack.shopaholic.world.shop.input.EntityShopInput;
import uk.joshiejack.shopaholic.world.shop.input.ItemShopInput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DepartmentMenu extends AbstractContainerMenu {
    public Shop shop;
    public Department department;
    public ShopTarget target;
    public boolean originalTarget;

    public DepartmentMenu(int windowID, Player player, @Nullable FriendlyByteBuf buf) {
        super(ShopaholicMenus.SHOP.get(), windowID);
        if (buf != null) {
            boolean reloadLastDepartment = buf.readBoolean();
            Department d = Shopaholic.ShopaholicRegistries.DEPARTMENTS.get(buf.readResourceLocation());
            BlockPos pos = BlockPos.of(buf.readVarLong());
            int entityID = buf.readVarInt();
            ItemStack stack = buf.readItem();
            byte type = buf.readByte();
            ShopInput<?> input = (type == BlockShopInput.ID ? new BlockShopInput(buf) : type == EntityShopInput.ID ? new EntityShopInput(buf) : new ItemShopInput(buf));
            target = new ShopTarget(player.level(), pos, player.level().getEntity(entityID), player, stack, input);
            shop = Shop.get(d);
            department = shop != null && reloadLastDepartment && shop.getLast().isValidFor(target, Condition.CheckType.SHOP_LISTING) ? shop.getLast() : d;
            shop = Shop.get(department);
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(player.getInventory(), j1 + l * 9 + 9, 275 + l * 18, 61 + j1 * 18));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(player.getInventory(), i1, 253, 61 + i1 * 18));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    public AbstractContainerMenu withData(Department department, ShopTarget target, boolean reloadLastDepartment) {
        this.target = target;
        this.shop = Shop.get(department);
        this.department = shop != null && reloadLastDepartment && shop.getLast().isValidFor(target, Condition.CheckType.SHOP_LISTING) ? shop.getLast() : department;
        this.shop = Shop.get(this.department);
        return this;
    }
}
