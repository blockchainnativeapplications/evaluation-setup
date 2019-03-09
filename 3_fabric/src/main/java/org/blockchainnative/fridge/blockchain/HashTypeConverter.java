package org.blockchainnative.fridge.blockchain;


import org.blockchainnative.convert.TypeConverter;
import org.blockchainnative.fridge.Hash;

/**
 * Converts an objects of type Hash to String and vice versa.
 * The order of the type parameters is irrelevant.
 *
 * The type converter is automatically detected by the framework because it has been registered for dependency injection
 * in {@link org.blockchainnative.fridge.ContractConfiguration#hashTypeConverter()}.
 *
 * @author Matthias Veit
 */
public class HashTypeConverter implements TypeConverter<Hash, String> {
    @Override
    public String to(Hash hash)
    {
        // TODO Convert hash to String
        // Check the methods provided by Hash
        return hash.getHashAsHexString();
    }

    @Override
    public Hash from(String string)
    {
        // TODO create a Hash object from String
        return new Hash(string);
    }
}
