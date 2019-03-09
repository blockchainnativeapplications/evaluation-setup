package org.blockchainnative.fridge.items;

import org.blockchainnative.fridge.Hash;
import org.blockchainnative.fridge.items.base.WeightSizableItem;

/**
 * @author Matthias Veit
 */
public class Cheese extends WeightSizableItem {

    public Cheese(String itemId, float price, float weightInKiloGram) {
        super(itemId, price, weightInKiloGram);
    }

    @Override
    public Hash getSHA256Hash() {
        return createHashFromArguments(getItemId(), priceFormat.format(getPrice()), weightFormat.format(getWeight()));
    }
}
