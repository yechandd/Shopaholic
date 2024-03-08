package uk.joshiejack.shopaholic.world.shop.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.shopaholic.api.shop.ListingType;

import java.util.List;

public record ItemStackListing(ItemStack stack) implements ListingType {
    public static final Codec<ItemStackListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(handler -> handler.stack)
    ).apply(instance, ItemStackListing::new));

    public static ItemStackListing of(Item item) {
        return new ItemStackListing(new ItemStack(item));
    }

    @Override
    public Icon createIcon() {
        return new ItemIcon(stack);
    }

    @Override
    public void purchase(Player player) {
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Override
    public Codec<? extends ListingType> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addTooltip(List<Component> list) {
        list.addAll(stack.getTooltipLines(null, TooltipFlag.NORMAL));
    }

    @Override
    public String id() {
        return RegistryHelper.id(stack.getItem()).toString();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getDisplayName() {
        return stack.getHoverName();
    }

    @Override
    public int getCount() {
        return stack.getCount();
    }

    public ItemStack get() {
        return stack;
    }

    @Override
    public boolean isValid() {
        return !stack.isEmpty();
    }
}
