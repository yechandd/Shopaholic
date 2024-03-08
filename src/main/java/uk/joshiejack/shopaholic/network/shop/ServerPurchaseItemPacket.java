package uk.joshiejack.shopaholic.network.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.shopaholic.world.bank.Bank;
import uk.joshiejack.shopaholic.world.bank.Vault;
import uk.joshiejack.shopaholic.network.AbstractPurchaseItemPacket;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.Listing;

@Packet(PacketFlow.SERVERBOUND)
public class ServerPurchaseItemPacket extends AbstractPurchaseItemPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("server_purchase_item");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ServerPurchaseItemPacket(Department department, Listing listing, int amount) {
        super(department, listing, amount);
    }

    public ServerPurchaseItemPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void handle(Player player) {
        if (listing.canPurchase(player, department.getStockLevels(player.level()), amount)) {
            if (purchase((ServerPlayer)player)) {
                PenguinNetwork.sendToClient((ServerPlayer) player, new ClientPurchaseItemPacket(department, listing, amount)); //Send the packet back
            }
        }
    }

    private boolean purchase(ServerPlayer player) {
        Vault vault = Bank.get((ServerLevel) player.level()).getVaultForPlayer(player);
        long cost = listing.getGoldCost(player, department, department.getStockLevels(player.level())); //TownHelper.getClosestTownToEntity(player, false).getShops().getSellValue(shop, purchasable); //TODO: Enable adjusted days
        long total = cost * amount;
        if (vault.getBalance() - total >= 0) {
            if (total >= 0) vault.decreaseBalance(player.level(), total);
            else vault.increaseBalance(player.level(), -total);
            for (int i = 0; i < amount; i++) {
                listing.purchase(player, department);
            }

            return true;
        }

        return false;
    }
}
