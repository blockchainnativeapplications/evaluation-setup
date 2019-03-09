package org.blockchainnative.fridge.items;

import org.blockchainnative.fridge.Hash;
import org.blockchainnative.fridge.items.base.PackageSizableItem;


/**
 * @author Matthias Veit
 */
public class Eggs extends PackageSizableItem {

    public Eggs(String itemId, float price, int amountPerPackage) {
        super(itemId, price, amountPerPackage);
    }

    @Override
    public Hash getSHA256Hash() {
        return createHashFromArguments(getItemId(), priceFormat.format(getPrice()), Integer.toString(getAmountPerPackage()));
    }
}
