package org.blockchainnative.warmup;

import io.reactivex.Observable;
import org.blockchainnative.annotations.ContractEvent;
import org.blockchainnative.annotations.ContractMethod;
import org.blockchainnative.ethereum.EthereumSmartContract;

/**
 * Sample Ethereum smart contract wrapper interface for 'HelloContract.sol' (see resources/HelloContract)
 *
 * @author Matthias Veit
 */
public interface HelloContract extends EthereumSmartContract<HelloContract> {

    /**
     * Deploys the wrapped smart contract on the connected Ethereum blockchain.
     *
     * @param greeting constructor parameter of the Solidity contract, represents the greeting to be used in invocations of 'hello'.
     */
    @ContractMethod(isSpecialMethod = true)
    void deploy(String greeting);

    // TODO Task 1.1.1: add method for smart contract function 'hello'
    // take a look a the function 'hello' defined in resources/HelloContract/HelloContract.sol
    // and declare a Java method that matches the functions signature

    // TODO Task 1.1.1: add event method for smart contract event 'greeted'
    // take a look a the event 'greeted' defined in resources/HelloContract/HelloContract.sol
    // and declare a Java method that matching the requirements defined by the annotation org.blockchainnative.annotations.ContractEvent

    /**
     * Sample event type for the event 'greeted' specified in 'HelloContract.sol'
     */
    class HelloEvent {
        private String name;

        public HelloEvent() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
