package org.blockchainnative.fridge.items;

import org.blockchainnative.fridge.Hash;
import org.blockchainnative.fridge.items.base.VolumeSizableItem;

/**
 * @author Matthias Veit
 */
public class Soda extends VolumeSizableItem {

    public Soda(String itemId, float price, float sizeInLiter) {
        super(itemId, price, sizeInLiter);
    }

    @Override
    public Hash getSHA256Hash() {
        return createHashFromArguments(getItemId(), priceFormat.format(getPrice()), volumeFormat.format(getVolume()));
    }
}
