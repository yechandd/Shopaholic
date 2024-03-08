package uk.joshiejack.shopaholic.world.bank;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import uk.joshiejack.shopaholic.api.bank.IBank;
import uk.joshiejack.shopaholic.client.bank.Wallet;

public class BankAPIImpl implements IBank {
    @Override
    public long getBalance(Player player) {
        return player.level().isClientSide() ? Wallet.getActive().getBalance() : Bank.get((ServerLevel) player.level()).getVaultForPlayer(player).getBalance();
    }

    @Override
    public void increaseBalance(Player player, long amount) {
        if (player.level() instanceof ServerLevel serverWorld)
            Bank.get(serverWorld).getVaultForPlayer(player).increaseBalance(player.level(), amount);
    }

    @Override
    public void decreaseBalance(Player player, long amount) {
        if (!player.level().isClientSide)
            Bank.get((ServerLevel) player.level()).getVaultForPlayer(player).decreaseBalance(player.level(), amount);
    }
}
