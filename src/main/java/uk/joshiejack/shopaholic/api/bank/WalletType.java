package uk.joshiejack.shopaholic.api.bank;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

public enum WalletType {
    PERSONAL {
        @OnlyIn(Dist.CLIENT)
        @Override
        public String getName(Player player) {
            return Component.translatable("gui.shopaholic.manager.account", player.getName()).getString();
        }
    }, SHARED {
        @OnlyIn(Dist.CLIENT)
        @Override
        public String getName(Player player) {
            return Component.translatable("gui.shopaholic.manager.account", PenguinTeams.getTeamForPlayer(player).getName()).getString();
        }
    };

    @OnlyIn(Dist.CLIENT)
    public String getName(Player player) {
        return name();
    }
}