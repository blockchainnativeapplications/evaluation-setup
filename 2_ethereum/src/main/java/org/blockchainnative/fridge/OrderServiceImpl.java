package org.blockchainnative.fridge;

import org.blockchainnative.fridge.blockchain.ReceiptContract;
import org.blockchainnative.fridge.items.*;
import org.blockchainnative.fridge.items.base.Item;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used by {@link Refrigerator} new items.
 *
 * @author Matthias Veit
 */
public class OrderServiceImpl implements OrderService {

    private static Set<Item> stock = new HashSet<>() {{
        add(new Butter("Butter", 2.50f, 0.250f));
        add(new Cheese("Gouda", 3.99f, 0.300f));
        add(new Eggs("Happy Chicken Eggs", 2.89f, 12));
        add(new Milk("Moo Milk", 1.49f, 1f));
        add(new Soda("Pepsi", 1.29f, 0.250f));
    }};

    private final ReceiptContract receiptContract;

    public OrderServiceImpl(ReceiptContract receiptContract) {
        this.receiptContract = receiptContract;
    }

    @Override
    public Order placeOrder(HashMap<Class<? extends Item>, Integer> itemsToBeBought) {

        var items = itemsToBeBought.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(getMatchingItemFromStock(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        var receipt = new Receipt(items, LocalDateTime.now());

        // TODO Use the ReceiptContract provided in the class' constructor to store the receipt hash on the blockchain


        return new Order(receipt, items);
    }

    private Item getMatchingItemFromStock(Class<? extends Item> itemType){
        return stock.stream()
                .filter(stockItem -> itemType.isAssignableFrom(stockItem.getClass()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Item of type '%s' is not in stock!", itemType.getName())));
    }
}
