package uk.joshiejack.shopaholic.api.shop;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import uk.joshiejack.shopaholic.world.shop.input.EntityShopInput;

public class ShopTarget {
    private Level world; //The world that this target is in
    private BlockPos pos; //The position of this target
    private Entity entity; //The entity associated with this target, EITHER the actual entity OR the player if it's an item or blockstate
    private Player player; //The player entity that was interacting with this target
    private ItemStack stack; //The stack that is interacting, whether it's relevant or not
    private ShopInput<?> input; //The input handler, for special situations

    @SuppressWarnings("unused")
    public ShopTarget() {}
    public ShopTarget(Level world, BlockPos pos, Entity entity, Player player, ItemStack stack, ShopInput<?> input) {
        this.world = world;
        this.pos = pos;
        this.entity = entity;
        this.player = player;
        this.stack = stack;
        this.input = input;
    }

    public static ShopTarget fromPlayer(Player player) {
        return new ShopTarget(player.level(), player.blockPosition(), player, player, player.getMainHandItem(), new EntityShopInput(player));
    }

    public static ShopTarget fromSource(CommandSourceStack source) {
        try {
            return fromPlayer(source.getPlayerOrException());
        } catch (CommandSyntaxException ex) { return null; }
    }

    public ShopTarget asPlayer() {
        return new ShopTarget(world, pos, player, player, stack, input);
    }

    public Level getLevel() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Entity getEntity() {
        return entity;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ShopInput<?> getInput() {
        return input;
    }
}