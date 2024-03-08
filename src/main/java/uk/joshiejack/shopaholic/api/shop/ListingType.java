package uk.joshiejack.shopaholic.api.shop;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.util.icon.Icon;

import java.util.List;

public interface ListingType {
    Codec<? extends ListingType> codec();

    /**
     * Return the count of this object,
     * for most items this will always be 1
     * an example where it's used is in itemstacks
     * @return  the count of this object
     */
    default int getCount() { return 1; }

    /**
     * Called when the player purchases this item, to give them the item/do an action, or whatever it is!
     * @param player        the player
     */
    void purchase(Player player);

    /**
     * The display name of this object, used in the shop screen, if no custom name is set
     * @return  the name of the object
     */

    Component getDisplayName();

    /**
     * Creates an icon to show in the shop
     * @return  the icon for this object
     */
    Icon createIcon();

    /**
     * Display the tooltip for this item, often just the name but can contain other data
     * this is only called if the item doesn't have a custom tooltip
     * @param list          the list of components to add to
     */
    default void addTooltip(List<Component> list) {}

    /**
     * Return the id of this object, try to keep it unique
     * @return  the id of this object
     */
    String id();

    /**
     * Return whether this object is valid for example if it is AIR it is not valid
     * @return  whether this object is valid
     */
    boolean isValid();

    interface Builder {
        /** Build the listing type
         *
         * @param shopLoadingData the shop loading data
         * @param database the database loaded event
         * @param data the data
         *
         * @return the listing type
         * **/
        ListingType build(ShopLoadingData shopLoadingData, DatabaseLoadedEvent database, String data);
    }
}