package org.blockchainnative.fridge.blockchain;


import org.blockchainnative.convert.TypeConverter;
import org.blockchainnative.fridge.Hash;

/**
 * Converts an objects of type Hash to byte[] and vice versa.
 * The order of the type parameters is irrelevant.
 *
 * The type converter is automatically detected by the framework because it has been registered for dependency injection
 * in {@link org.blockchainnative.fridge.ContractConfiguration#hashTypeConverter()}.
 *
 * @author Matthias Veit
 */
public class HashTypeConverter implements TypeConverter<Hash, byte[]> {
    @Override
    public byte[] to(Hash hash)
    {
        // TODO Convert hash to byte[]
        // Check the methods provided by Hash
        return hash.getHashAsBytes();
    }

    @Override
    public Hash from(byte[] bytes)
    {
        // TODO create a Hash object from byte[]
        return new Hash(bytes);
    }
}
