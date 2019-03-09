package org.blockchainnative.fridge.items.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.blockchainnative.fridge.Hash;
import org.blockchainnative.fridge.Hashable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Base type for all items, allows to calculate their hash values.
 *
 * @author Matthias Veit
 */
public abstract class Item implements Hashable {

    static {
        if(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    protected static final DecimalFormat priceFormat = new DecimalFormat("#.00");

    private String itemId;
    private float price;

    private final MessageDigest digest;

    public Item(String itemId, float price) {
        this.itemId = itemId;
        this.price = roundPrice(price);
        try {
            this.digest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get instance of SHA256 message digest!", e);
        }
    }

    protected static float roundPrice(float price){
        if(price < 0)
            throw new IllegalArgumentException("price needs to be greater than 0");

        return (float) roundToNDecimalPlaces(price, 2);
    }

    protected static double roundToNDecimalPlaces(double number, int decimalPlaces) {
        return Math.round(number * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = roundPrice(price);
    }

    protected MessageDigest getDigest() {
        return digest;
    }

    protected Hash createHashFromArguments(String... args){
        var joiner = new StringJoiner("|");

        for (var arg : args){
            joiner.add(Objects.toString(arg));
        }

        var bytes =  joiner.toString().getBytes(StandardCharsets.UTF_8);

        return new Hash(digest.digest(bytes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        if (Float.compare(item.price, price) != 0) return false;
        if (itemId != null ? !itemId.equals(item.itemId) : item.itemId != null) return false;
        return digest != null ? digest.equals(item.digest) : item.digest == null;
    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (digest != null ? digest.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Item.class.getSimpleName() + "[", "]")
                .add("itemId='" + itemId + "'")
                .add("price=" + price)
                .toString();
    }
}
