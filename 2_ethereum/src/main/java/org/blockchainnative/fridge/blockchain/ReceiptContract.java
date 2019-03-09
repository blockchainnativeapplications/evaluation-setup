package org.blockchainnative.fridge.blockchain;

import org.blockchainnative.annotations.ContractMethod;
import org.blockchainnative.annotations.SmartContract;
import org.blockchainnative.ethereum.EthereumSmartContract;
import org.blockchainnative.fridge.Hash;

import java.util.List;

/**
 * Smart contract wrapper interface for ReceiptContract provided in the resource folder
 *
 * @author Matthias Veit
 */
@SmartContract
public interface ReceiptContract extends EthereumSmartContract<ReceiptContract> {

    /**
     * Deploys the smart contract to the blockchain.
     *
     */
    @ContractMethod(isSpecialMethod = true)
    void deploy();

    /**
     * Stores the hash value of a receipt in on the blockchain
     *
     * @param receiptHash hash value to be stored
     */
    @ContractMethod
    void storeReceipt(Hash receiptHash);

    /**
     * Returns all receipt hashes stored in the smart contract
     *
     * @return list of hash values
     */
    @ContractMethod
    List<Hash> getReceiptHashes();
}
