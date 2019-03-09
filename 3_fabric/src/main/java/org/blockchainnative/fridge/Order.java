package org.blockchainnative.fridge;

import org.blockchainnative.fridge.items.base.Item;

import java.util.Collections;
import java.util.Map;

/**
 * Represents an order of {@link Item}.
 *
 * @author Matthias Veit
 */
public class Order {
    private final Receipt receipt;
    private final Map<Item, Integer> items;

    public Order(Receipt receipt, Map<Item, Integer> items) {
        this.receipt = receipt;
        this.items = Collections.unmodifiableMap(items);
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (receipt != null ? !receipt.equals(order.receipt) : order.receipt != null) return false;
        return items != null ? items.equals(order.items) : order.items == null;
    }

    @Override
    public int hashCode() {
        int result = receipt != null ? receipt.hashCode() : 0;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
