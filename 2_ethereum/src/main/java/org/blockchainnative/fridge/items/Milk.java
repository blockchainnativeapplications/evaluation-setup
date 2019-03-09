package org.blockchainnative.fridge.items;

import org.blockchainnative.fridge.Hash;
import org.blockchainnative.fridge.items.base.VolumeSizableItem;

/**
 * @author Matthias Veit
 */
public class Milk extends VolumeSizableItem {

    public Milk(String name, float price, float sizeInLiter) {
        super(name, price, sizeInLiter);
    }

    @Override
    public Hash getSHA256Hash() {
        return createHashFromArguments(getItemId(), priceFormat.format(getPrice()), volumeFormat.format(getVolume()));
    }
}
