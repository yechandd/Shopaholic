package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.world.inventory.DepartmentMenu;
import uk.joshiejack.shopaholic.network.AbstractPacketSyncDepartment;
import uk.joshiejack.shopaholic.world.shop.Department;

@Packet(PacketFlow.SERVERBOUND)
public class SwitchDepartmentPacket extends AbstractPacketSyncDepartment {
    public static final ResourceLocation ID = PenguinLib.prefix("switch_department");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    @SuppressWarnings("unused")
    public SwitchDepartmentPacket(Department department) {
        super(department);
    }

    public SwitchDepartmentPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Player player) {
        if (player.containerMenu instanceof DepartmentMenu)
            department.open(((DepartmentMenu) player.containerMenu).target, false);
    }
}
