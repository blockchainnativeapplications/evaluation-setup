package org.blockchainnative.fridge.blockchain;

import io.reactivex.Observable;
import org.blockchainnative.annotations.ContractEvent;
import org.blockchainnative.annotations.ContractMethod;
import org.blockchainnative.annotations.SmartContract;
import org.blockchainnative.annotations.SpecialArgument;
import org.blockchainnative.fabric.Constants;
import org.blockchainnative.fabric.FabricSmartContract;
import org.blockchainnative.fridge.Hash;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

/**
 * Smart contract wrapper interface for ReceiptContract provided in the resource folder
 *
 * @author Matthias Veit
 */
@SmartContract
public interface ReceiptContract extends FabricSmartContract<ReceiptContract> {

    /**
     * Installs the smart contract on the peers defined by {@code targetPeerNames} with the user context {@code user}
     *
     * @param user user context to be used to install the smart contract
     * @param targetPeerNames name of the peers to install the smart contract on
     */
    @ContractMethod(isSpecialMethod = true)
    void install(@SpecialArgument(Constants.USER_ARGUMENT)User user, @SpecialArgument(Constants.TARGET_PEERS_ARGUMENT) Set<String> targetPeerNames);

    /**
     * Instantiates the smart contract on all peers registered in the contract info {@link org.blockchainnative.fabric.metadata.FabricContractInfo#setTargetPeerNames(Set)}.
     *
     * @param user user context to be used to instantiate smart contract
     */
    @ContractMethod(isSpecialMethod = true)
    void instantiate(@SpecialArgument(Constants.USER_ARGUMENT) User user);

    /**
     * Stores the hash value of a receipt in on the blockchain
     *
     * @param receiptHash hash value to be stored
     */
    @ContractMethod
    void storeReceipt(Hash receiptHash);

    /**
     * Returns an observable representing the 'receiptAdded' event.
     *
     * @return event observable
     */
    @ContractEvent("receiptAdded")
    Observable<ReceiptAddedEvent> onHashAdded();

    class ReceiptAddedEvent {

        public Hash sha256Hash;
    }
}
