package uk.joshiejack.shopaholic.world.shipping;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.shopaholic.event.ItemShippedEvent;
import uk.joshiejack.shopaholic.network.shipping.ShipPacket;
import uk.joshiejack.shopaholic.network.shipping.SyncLastSoldPacket;
import uk.joshiejack.shopaholic.network.shipping.SyncSoldPacket;
import uk.joshiejack.shopaholic.world.bank.Bank;
import uk.joshiejack.shopaholic.world.bank.Vault;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Shipping implements INBTSerializable<CompoundTag> {
    private final Cache<ItemStack, Integer> countCache = CacheBuilder.newBuilder().build();
    private final Market market;
    private final UUID uuid;
    private boolean shared;
    private final Set<SoldItem> lastSell = Sets.newHashSet();
    private final Set<SoldItem> toSell = Sets.newHashSet();
    private final Set<SoldItem> sold = Sets.newHashSet();

    public Shipping(Market market, UUID uuid) {
        this.market = market;
        this.uuid = uuid;
    }

    public Shipping shared() {
        this.shared = true;
        return this;
    }

    public int getCount(ItemStack stack) {
        try {
            return countCache.get(stack, () -> {
                int total = 0;
                for (SoldItem s: sold) {
                    if (s.matches(stack)) return s.getStack().getCount();
                }

                return total;
            });
        } catch (ExecutionException e) {
            return 0;
        }
    }

    public void syncToPlayer(ServerPlayer player) {
        PenguinNetwork.sendToClient(player, new SyncSoldPacket(sold));
        PenguinNetwork.sendToClient(player, new SyncLastSoldPacket(lastSell));
    }

    public Set<SoldItem> getSold() {
        return sold;
    }

    public void add(ItemStack stack) {
        long value = ShippingRegistry.getValue(stack);
        //Add the tax
        if (value > 0) {
            for (SoldItem sold : toSell) {
                if (sold.matches(stack)) {
                    sold.merge(stack, value * stack.getCount());
                    market.setDirty();
                    return; //Found the match, so exit
                }
            }

            toSell.add(new SoldItem(stack, value * stack.getCount()));
            market.setDirty(); //Save!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    public void onNewDay(ServerLevel world) {
        if (!toSell.isEmpty()) {
            Vault vault = shared ? Bank.get(world).getVaultForTeam(uuid).shared() : Bank.get(world).getVaultForTeam(uuid).personal();
            toSell.forEach(holder -> vault.increaseBalance(world, holder.gold)); //Increase the vault for this players uuid
            CompoundTag tag = new CompoundTag();
            tag.put("ToSell", writeHolderCollection(toSell));
            if (shared) PenguinNetwork.sendToTeam(world, uuid, new ShipPacket(tag));
            else PenguinNetwork.sendToClient((ServerPlayer) world.getPlayerByUUID(uuid), new ShipPacket(tag));

            PenguinTeam team = shared ? PenguinTeams.getTeamFromID(world, uuid) : PenguinTeams.getTeamForPlayer(world, uuid);
            toSell.forEach(holder -> {
                NeoForge.EVENT_BUS.post(new ItemShippedEvent(world, team, holder.stack, holder.gold)); //Statistics go to the team, no matter what
                boolean merged = false;
                for (SoldItem sold: this.sold) {
                    if (sold.matches(holder.stack)) {
                        sold.merge(holder); //Merge in the holder
                        merged = true;
                    }
                }

                if (!merged) this.sold.add(holder); //Add the holder if it doesn't exist yet
            });

            lastSell.clear();
            lastSell.addAll(toSell); //Add everything we just sold
            if (shared) PenguinNetwork.sendToTeam(world, uuid, new SyncLastSoldPacket(lastSell)); //Send the new last sold info to the clients
            else PenguinNetwork.sendToClient((ServerPlayer) world.getPlayerByUUID(uuid), new SyncLastSoldPacket(lastSell));
            countCache.invalidateAll();
            toSell.clear();
            market.setDirty();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("LastSold", writeHolderCollection(lastSell));
        tag.put("ToSell", writeHolderCollection(toSell));
        tag.put("Sold", writeHolderCollection(sold));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readHolderCollection(nbt.getList("LastSold", 10), lastSell);
        readHolderCollection(nbt.getList("ToSell", 10), toSell);
        readHolderCollection(nbt.getList("Sold", 10), sold);
    }

    public static void readHolderCollection(ListTag list, Collection<SoldItem> collection) {
        collection.clear();
        list.forEach(data -> collection.add(new SoldItem((CompoundTag)data)));
    }

    public static ListTag writeHolderCollection(Set<SoldItem> set) {
        ListTag list = new ListTag();
        set.forEach(item -> list.add(item.serializeNBT()));
        return list;
    }

    public static class SoldItem implements INBTSerializable<CompoundTag> {
        private ItemStack stack;
        private long gold;

        public SoldItem(CompoundTag data) {
            this.deserializeNBT(data);
        }

        public SoldItem(ItemStack stack, long gold) {
            this.stack = stack;
            this.gold = gold;
        }

        public ItemStack getStack() {
            return stack;
        }

        public long getValue() {
            return gold;
        }

        public boolean matches(ItemStack stack) {
            return ItemStack.isSameItemSameTags(stack, this.stack);
        }

        public boolean matches(Item item) {
            return stack.getItem() == item;
        }

        public boolean matches(TagKey<Item> tag) {
            return stack.is(tag);
        }

        public void merge(SoldItem holder) {
            this.stack.grow(holder.stack.getCount());
            this.gold += holder.getValue();
        }

        void merge(ItemStack stack, long value) {
            this.stack.grow(stack.getCount());
            this.gold += value;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("Item", stack.save(new CompoundTag()));
            tag.putInt("ItemCount", stack.getCount());
            tag.putLong("Gold", gold);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            stack = ItemStack.of(nbt.getCompound("Item"));
            stack.setCount(nbt.getInt("ItemCount"));
            gold = nbt.getLong("Gold");
        }
    }
}
