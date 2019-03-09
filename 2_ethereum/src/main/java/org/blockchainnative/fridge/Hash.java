package org.blockchainnative.fridge;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * Custom representation of a hash value.
 *
 * @author Matthias Veit
 */
public class Hash {
    private final byte[] bytes;

    public Hash(byte[] bytes) {
        this.bytes = bytes;
    }

    public Hash(String hexString) {
        this.bytes = Hex.decode(hexString);
    }

    public byte[] getHashAsBytes() {
        return bytes;
    }

    public String getHashAsHexString() {
        return Hex.toHexString(bytes);
    }

    @Override
    public String toString() {
        return getHashAsHexString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hash)) return false;

        Hash hash = (Hash) o;

        return Arrays.equals(bytes, hash.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
