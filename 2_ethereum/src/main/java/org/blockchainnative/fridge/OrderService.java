package org.blockchainnative.fridge;

import org.blockchainnative.fridge.items.base.Item;

import java.util.HashMap;

/**
 * @author Matthias Veit
 */

public interface OrderService {

    Order placeOrder(HashMap<Class<? extends Item>, Integer> itemsToBeBought);

}
