package org.blockchainnative.fridge;

import org.blockchainnative.fridge.items.base.Item;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the receipt of an order.
 *
 * @author Matthias Veit
 */
public class Receipt implements Hashable{
    private final Map<Item, Integer> items;
    private final LocalDateTime timestamp;
    private final float sum;
    private final Hash sha256Hash;

    public Receipt(Map<Item, Integer> items, LocalDateTime timestamp) {
        if(items == null) {
            items = new HashMap<>();
        }
        this.items = items;
        this.timestamp = timestamp;

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        this.sum = items.entrySet().stream()
                .map(itemIntegerEntry -> itemIntegerEntry.getKey().getPrice() * itemIntegerEntry.getValue())
                .reduce((aFloat, aFloat2) -> aFloat + aFloat2)
                .orElse(0f);
        this.sha256Hash = items.entrySet().stream()
                .map(itemIntegerEntry -> {

                    var combinedHash = Integer.toHexString( itemIntegerEntry.getValue()) + "|" + itemIntegerEntry.getKey().getSHA256Hash();

                    return new Hash(digest.digest(combinedHash.getBytes(StandardCharsets.UTF_8)));
                })
                .sorted(Comparator.comparing(Hash::getHashAsHexString))
                .reduce((s1, s2) -> {
                    var combinedHash = s1.getHashAsHexString() + "|" + s2.getHashAsHexString();
                    return new Hash(digest.digest(combinedHash.getBytes(StandardCharsets.UTF_8)));
                }).orElse(new Hash(digest.digest("".getBytes(StandardCharsets.UTF_8))));
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public float getSum() {
        return sum;
    }

    @Override
    public Hash getSHA256Hash() {
        return sha256Hash;
    }
}
