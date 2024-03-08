package uk.joshiejack.shopaholic.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import uk.joshiejack.shopaholic.world.inventory.EconomyManagerMenu;

public class ManagerCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("manager")
                .executes(ctx -> {
                    ctx.getSource().getPlayerOrException()
                            .openMenu(new SimpleMenuProvider((id, inv, p) -> new EconomyManagerMenu(id),
                                    Component.translatable("gui.shopaholic.manager")));
                    return 1;
                });
    }
}
