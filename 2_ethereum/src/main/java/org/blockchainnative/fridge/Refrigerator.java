package org.blockchainnative.fridge;

import org.blockchainnative.fridge.items.base.Item;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Represents a smart refrigerator.
 * Items can be added and removed via {@link Refrigerator#addItem(Item)}, {@link Refrigerator#takeItem(Item)} {@link Refrigerator#takeItem(Class)}.
 * At any point, it can be instructed to refill itself so that the minimum supply is reached again via {@link Refrigerator#restock()}.
 *
 * @author Matthias Veit
 */
public class Refrigerator {

    private final OrderService orderService;
    private Map<Item, Integer> contents;
    private Map<Class<? extends Item>, Integer> minimumSupply;

    public Refrigerator(OrderService orderService) {
        this.orderService = orderService;
        this.contents = new HashMap<>();
        this.minimumSupply = new HashMap<>();
    }

    public void setMinimumSupply(Map<Class<? extends Item>, Integer> minimumSupply) {
        if (minimumSupply == null) throw new IllegalArgumentException("minimumSupply must not be null");

        this.minimumSupply = new HashMap<>(minimumSupply);
    }

    public Map<Class<? extends Item>, Integer> getMinimumSupply(){
        return Collections.unmodifiableMap(minimumSupply);
    }

    public Map<Item, Integer> getContents(){
        return Collections.unmodifiableMap(contents);
    }

    public void addItem(Item item){
        if(item == null) throw new IllegalArgumentException("item must not be null");

        if(this.contents.containsKey(item)){
            this.contents.put(item, this.contents.get(item) + 1);
        } else {
            this.contents.put(item, 1);
        }
    }

    public Item takeItem(Item item){
        if(item == null) throw new IllegalArgumentException("item must not be null");

        if(this.contents.containsKey(item) && this.contents.get(item) > 0){
            this.contents.put(item, this.contents.get(item) - 1);

            return item;
        } else {
           throw new IllegalStateException(String.format("Item '%s' not found in refrigerator", item.getItemId()));
        }
    }

    public Item takeItem(Class<? extends Item> itemType){
        if(itemType == null) throw new IllegalArgumentException("item type must not be null");

        var entry = contents.entrySet().stream()
                .filter(e -> itemType.isAssignableFrom(e.getKey().getClass()) && e.getValue() > 0)
                .findAny();

        if(entry.isPresent()){
            this.contents.put(entry.get().getKey(), this.contents.get(entry.get().getKey()) - 1);

            return entry.get().getKey();
        } else {
            throw new IllegalStateException(String.format("No item of type '%s' found in refrigerator", itemType.getName()));
        }

    }

    public Optional<Receipt> restock(){
        var itemsToBeBought = new HashMap<Class<? extends Item>, Integer>();

        var contentTypeMap = extractTypeFromContents();
        for(var entry : minimumSupply.entrySet()){
            if(!contentTypeMap.containsKey(entry.getKey())){
                itemsToBeBought.put(entry.getKey(), entry.getValue());
            } else {
                var currentAmount = contentTypeMap.get(entry.getKey());
                if(currentAmount < entry.getValue()){
                    itemsToBeBought.put(entry.getKey(), entry.getValue() - currentAmount);
                }
            }
        }
        if(!itemsToBeBought.isEmpty()){
            var order = orderService.placeOrder(itemsToBeBought);

            // add items to content
            order.getItems().entrySet()
                    .forEach(entry ->
                            IntStream.range(0, entry.getValue())
                                    .forEach(i -> addItem(entry.getKey())));

            return Optional.ofNullable(order.getReceipt());
        } else {
            return Optional.empty();
        }
    }

    private Map<Class<? extends Item>, Integer> extractTypeFromContents(){
        var map = new HashMap<Class<? extends Item>, Integer>();

        contents.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey().getClass(), entry.getValue()))
                .forEach(entry ->  {
                    if(map.containsKey(entry.getKey())){
                        map.put(entry.getKey(), map.get(entry.getKey()) + entry.getValue());
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                });

        return map;
    }
}
