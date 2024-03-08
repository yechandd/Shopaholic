package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import joptsimple.internal.Strings;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.api.shop.ListingType;

import javax.annotation.Nonnull;
import java.util.Objects;

public record CommandListing(String command) implements CommandSource, ListingType {
    public static final Codec<CommandListing> CODEC = Codec.STRING.xmap(CommandListing::new, CommandListing::command);
    public static final Lazy<Icon> ICON = Lazy.of(() -> new ItemIcon(Items.COMMAND_BLOCK.getDefaultInstance()));

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return Component.literal(command);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Icon createIcon() {
        return ICON.get();
    }

    @SuppressWarnings("ConstantConditions")
    private CommandSourceStack createCommandSourceStack(Player player) {
        return new CommandSourceStack(this, player.position(), player.getRotationVector(), (ServerLevel) player.level(), 2, player.getName().getString(),
                player.getDisplayName(), Objects.requireNonNull(player.level().getServer()), null);
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @Override
    public void purchase(Player player) {
        if (!player.level().isClientSide && !Strings.isNullOrEmpty(command)) {
            MinecraftServer minecraftserver = player.level().getServer();
            try {
                CommandSourceStack commandsource = createCommandSourceStack(player);
                assert minecraftserver != null;
                minecraftserver.getCommands().performPrefixedCommand(commandsource, command);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command purchase");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", () -> command);
                crashreportcategory.setDetail("Name", () -> player.getName().getString());
                throw new ReportedException(crashreport);
            }
        }
    }
    @Override
    public void sendSystemMessage(@Nonnull Component text) {}

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }

    @Override
    public String id() {
        return String.valueOf(command.hashCode());
    }

    @Override
    public boolean isValid() {
        return !Strings.isNullOrEmpty(command);
    }
}

