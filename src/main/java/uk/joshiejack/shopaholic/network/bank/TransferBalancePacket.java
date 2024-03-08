package uk.joshiejack.shopaholic.network.bank;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.world.bank.Bank;
import uk.joshiejack.shopaholic.world.bank.Vault;

import java.util.UUID;

@Packet(PacketFlow.SERVERBOUND)
public class TransferBalancePacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("transfer_balance");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private final WalletType from;
    private final long gold;

    public TransferBalancePacket(WalletType type, long gold) {
        this.from = type;
        this.gold = gold;
    }

    public TransferBalancePacket(FriendlyByteBuf from) {
        this.from = from.readBoolean() ? WalletType.SHARED : WalletType.PERSONAL;
        gold = from.readLong();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeBoolean(from == WalletType.SHARED);
        to.writeLong(gold);
    }

    @Override
    public void handle(Player player) {
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        UUID playerUUID = player.getUUID();
        Vault teamVault = Bank.get((ServerLevel) player.level()).getVaultForTeam(team.getID());
        Vault playerVault = Bank.get((ServerLevel) player.level()).getVaultForTeam(playerUUID);
        if (from == WalletType.PERSONAL) {
            long actual = Math.min(gold, playerVault.getBalance());
            playerVault.personal().setBalance(player.level(), playerVault.getBalance() - actual);
            teamVault.shared().setBalance(player.level(), teamVault.getBalance() + actual);
        } else {
            long actual = Math.min(gold, teamVault.getBalance());
            teamVault.shared().setBalance(player.level(), teamVault.getBalance() - actual);
            playerVault.personal().setBalance(player.level(), playerVault.getBalance() + actual);
        }
    }
}
