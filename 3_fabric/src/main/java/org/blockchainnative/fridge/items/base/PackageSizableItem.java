package org.blockchainnative.fridge.items.base;

import java.util.StringJoiner;

/**
 * @author Matthias Veit
 */
public abstract class PackageSizableItem extends Item {

    private int amountPerPackage;

    public PackageSizableItem(String itemId, float price, int amountPerPackage) {
        super(itemId, price);
        this.amountPerPackage = ensurePackageSize(amountPerPackage);
    }

    public int getAmountPerPackage() {
        return amountPerPackage;
    }

    public void setAmountPerPackage(int amountPerPackage) {
        this.amountPerPackage = ensurePackageSize(amountPerPackage);
    }

    protected static int ensurePackageSize(int amount) {
        if(amount < 1)
            throw new IllegalArgumentException("amountPerPackage needs to be greater than 0");

        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageSizableItem)) return false;
        if (!super.equals(o)) return false;

        PackageSizableItem that = (PackageSizableItem) o;

        return amountPerPackage == that.amountPerPackage;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + amountPerPackage;
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PackageSizableItem.class.getSimpleName() + "[", "]")
                .add("amountPerPackage=" + amountPerPackage)
                .toString();
    }
}
