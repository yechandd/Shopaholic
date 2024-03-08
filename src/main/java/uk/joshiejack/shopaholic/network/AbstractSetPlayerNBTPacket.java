package uk.joshiejack.shopaholic.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;

public abstract class AbstractSetPlayerNBTPacket implements PenguinPacket {
    private final String tagName;

    public AbstractSetPlayerNBTPacket(String tag) {
        this.tagName = tag;
    }

    @Override
    public void handle(Player player) {
        if (!player.getPersistentData().contains(tagName))
            player.getPersistentData().put(tagName, new CompoundTag());
        setData(player.getPersistentData().getCompound(tagName));
    }

    protected abstract void setData(CompoundTag compound);
}
