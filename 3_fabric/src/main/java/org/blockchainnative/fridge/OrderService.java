package org.blockchainnative.fridge;

import org.blockchainnative.fridge.items.base.Item;

import java.util.HashMap;

/**
 * Used by {@link Refrigerator} new items.
 *
 * @author Matthias Veit
 */
public interface OrderService {

    Order placeOrder(HashMap<Class<? extends Item>, Integer> itemsToBeBought);

}
