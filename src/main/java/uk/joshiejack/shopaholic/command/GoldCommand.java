package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.command.EnumArgument;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.api.bank.WalletType;
import uk.joshiejack.shopaholic.world.bank.Bank;
import uk.joshiejack.shopaholic.world.bank.Vault;

import javax.annotation.Nullable;

public class GoldCommand {
    @Nullable
    private static Vault getVault(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        WalletType type = ctx.getArgument("type", WalletType.class);
        Bank bank = Bank.get(ctx.getSource().getLevel());
        Player player = EntityArgument.getPlayer(ctx, "player");
        return type == WalletType.PERSONAL
                ? bank.getVaultForPlayer(player) : bank.getVaultForTeam(PenguinTeams.getTeamForPlayer(player).getID());
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("gold")
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("type", EnumArgument.enumArgument(WalletType.class))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    Vault vault = getVault(ctx);
                                                    if (vault != null) {
                                                        vault.setBalance(ctx.getSource().getLevel(), vault.getBalance() + IntegerArgumentType.getInteger(ctx, "amount"));
                                                        return 1;
                                                    } else return 0;
                                                })))))

                .then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("type", EnumArgument.enumArgument(WalletType.class))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    Vault vault = getVault(ctx);
                                                    if (vault != null) {
                                                        vault.setBalance(ctx.getSource().getLevel(), IntegerArgumentType.getInteger(ctx, "amount"));
                                                        return 1;
                                                    } else return 0;
                                                })))));
    }
}
