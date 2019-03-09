package org.blockchainnative.fridge.items.base;

import java.text.DecimalFormat;
import java.util.StringJoiner;

/**
 * @author Matthias Veit
 */
public abstract class VolumeSizableItem extends Item {

    protected static final DecimalFormat volumeFormat = new DecimalFormat("#.000");
    private float volume;

    public VolumeSizableItem(String itemId, float price, float volume) {
        super(itemId, price);
        this.volume = roundVolume(volume);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = roundVolume(volume);
    }

    protected static float roundVolume(float price){
        if(price < 0)
            throw new IllegalArgumentException("volume needs to be greater than 0");

        return (float) roundToNDecimalPlaces(price, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VolumeSizableItem)) return false;
        if (!super.equals(o)) return false;

        VolumeSizableItem that = (VolumeSizableItem) o;

        return Float.compare(that.volume, volume) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (volume != +0.0f ? Float.floatToIntBits(volume) : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VolumeSizableItem.class.getSimpleName() + "[", "]")
                .add("volume=" + volume)
                .toString();
    }
}
