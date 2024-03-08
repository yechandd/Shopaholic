package uk.joshiejack.shopaholic.api.shop;

public interface IStockMechanic {
    /**
     * @return the maximum stock level for this item
     */
    int maximum();
    /**
     * @return the amount to increase the stock level by each day
     */
    int increase();
}
