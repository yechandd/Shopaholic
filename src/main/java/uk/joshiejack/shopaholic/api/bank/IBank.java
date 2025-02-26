package uk.joshiejack.shopaholic.api.bank;

import net.minecraft.world.entity.player.Player;

public interface IBank {
    /**
     * Grab the current balance for this player
     * It will return the correct balance for this player or team depending on which they are currently using
     * @param player        the player to get the balance of
     * @return   the balance
     */
    long getBalance(Player player);

    /**
     * Use this to decrease the balance of the player by the set amount
     * This MUST be called server side, it does nothing on the client
     *
     * @param player        the player to increase the balance of
     * @param amount        the amount to increase it by
     */
    void increaseBalance(Player player, long amount);

    /**
     * Use this to decrease the balance of the player by the set amount
     * This MUST be called server side, it does nothing on the client
     *
     * @param player        the player to decrease the balance of
     * @param amount        the amount to decrease it by
     */
    void decreaseBalance(Player player, long amount);
}
