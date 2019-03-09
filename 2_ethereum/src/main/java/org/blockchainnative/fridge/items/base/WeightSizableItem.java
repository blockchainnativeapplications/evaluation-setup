package org.blockchainnative.fridge.items.base;

import java.text.DecimalFormat;
import java.util.StringJoiner;

/**
 * @author Matthias Veit
 */
public abstract class WeightSizableItem extends Item {

    protected static final DecimalFormat weightFormat = new DecimalFormat("#.000");

    private float weight;

    public WeightSizableItem(String itemId, float price, float weight) {
        super(itemId, price);
        this.weight = roundWeight(weight);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = roundWeight(weight);
    }

    protected static float roundWeight(float weight){
        if(weight <= 0)
            throw new IllegalArgumentException("weight needs to be greater than 0");

        return (float) roundToNDecimalPlaces(weight, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeightSizableItem)) return false;
        if (!super.equals(o)) return false;

        WeightSizableItem that = (WeightSizableItem) o;

        return Float.compare(that.weight, weight) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (weight != +0.0f ? Float.floatToIntBits(weight) : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeightSizableItem.class.getSimpleName() + "[", "]")
                .add("weight=" + weight)
                .toString();
    }
}
