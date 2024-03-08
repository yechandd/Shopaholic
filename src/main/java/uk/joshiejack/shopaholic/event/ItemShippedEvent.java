package uk.joshiejack.shopaholic.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.LevelEvent;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;

public class ItemShippedEvent extends LevelEvent {
    private final PenguinTeam team;
    private final ItemStack shipped;
    private final long value;

    public ItemShippedEvent(Level world, PenguinTeam team, ItemStack shipped, long value) {
        super(world);
        this.team = team;
        this.shipped = shipped;
        this.value = value;
    }

    public PenguinTeam getTeam() {
        return team;
    }

    public ItemStack getShipped() {
        return shipped;
    }

    public long getValue() {
        return value;
    }
}
