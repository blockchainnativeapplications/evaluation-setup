package org.blockchainnative.warmup;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import org.blockchainnative.convert.TypeConverters;
import org.blockchainnative.ethereum.EthereumContractWrapperGenerator;
import org.blockchainnative.ethereum.builder.EthereumContractInfoBuilder;
import org.blockchainnative.ethereum.metadata.EthereumContractInfo;
import org.blockchainnative.ethereum.serialization.EthereumMetadataModule;
import org.blockchainnative.registry.FileSystemContractRegistry;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Warm up exercise to familiarize with the way the blockchain-native-applications framework is intended to be used.
 *
 * Before executing this example, make sure to have the provided Ethereum test network up and running.
 *
 * @author Matthias Veit
 */
public class WarmUp {

    public static void main(String[] args) throws IOException {

        // TODO Task 1.1.2:  create a contract info object for HelloContract using EthereumContractInfoBuilder
        // set the contract binary and ABI and make sure that the method 'hello' and the event 'greeted' are registered
        // explicitly or annotated with 'ContractMethod'/'ContractEvent' on the interface.
        var identifier = "helloContract";
        EthereumContractInfo<HelloContract> contractInfo = new EthereumContractInfoBuilder<>(HelloContract.class)
                .withIdentifier(identifier)
                .withAbi(new File(WarmUp.class.getClassLoader().getResource("HelloContract/HelloContract.abi").getFile()))
                .withBinary(new File(WarmUp.class.getClassLoader().getResource("HelloContract/HelloContract.bin").getFile()))
                .build();

        // TODO Task 1.1.3:  create an instance of FileSystemContractRegistry
        //  - The basePath shall point to the folder "contractInfo" in the project directory
        //  - Use registerObjectMapperModule() to add an instance of 'org.blockchainnative.ethereum.serialization.EthereumMetadataModule' so that the registry knows
        //    how to correctly serialize and deserialize EthereumContractInfo objects
        //  - Call load() on the registry to pickup previously persisted contract info objects
        //  - Lastly add the contract info object to the registry
        //    Beware that in subsequent executions the contract info will be loaded from the file system, therefore make sure
        //    to only add the object created in 1.1.2 if the registry does not yet contain an item with the same identifier.
        FileSystemContractRegistry contractRegistry = new FileSystemContractRegistry(Paths.get("contractInfo"));
        contractRegistry.registerObjectMapperModule(new EthereumMetadataModule());
        contractRegistry.load();
        contractRegistry.addContractInfoIfNotExisting(contractInfo);

        // TODO Task 1.1.4:  create an EthereumContractWrapperGenerator with the use the class' static methods
        EthereumContractWrapperGenerator contractWrapperGenerator = new EthereumContractWrapperGenerator(getClientFactory(), getTransactionManagerFactory(), getTypeConverters());

        // TODO Task 1.1.4:  generate a wrapper for the HelloContract interface using the EthereumContractWrapperGenerator
        // Use the contractRegistry to retrieve the contract info by its identifier instead of using the one created in Task 1.1.2
        HelloContract helloContract = contractWrapperGenerator.generate(contractRegistry.getContractInfo(identifier));

        // TODO Task 1.1.5: verify if the contract is deployed by checking the contract info's address and deploy the helloContract if required
        // Use the contract wrapper to retrieve the contract info
        if(!helloContract.getContractInfo().isDeployed()){
            helloContract.deploy("Hello");
        }

        // TODO Task 1.1.6:  call subscribe on the observable obtained through the contract's 'greeted' event method using the observer from getHelloEventObserver()
        helloContract.greeted().subscribe(getHelloEventObserver());


        // TODO Task 1.1.7: call the HelloContract, greet somebody and print the greeting
        System.out.println(helloContract.hello("World"));

        // TODO Task 1.1.8:  persist the contract info by using the registry
        contractRegistry.persist();

    }

    private static Web3j web3j;

    /**
     * Retrieves an observer that prints the name contained in {@code HelloContract.HelloEvent} and disposes after an receiving a single.
     *
     * @return observer for {@code HelloContract.HelloEvent} objects
     */
    private static Observer<HelloContract.HelloEvent> getHelloEventObserver(){
        return new DisposableObserver<HelloContract.HelloEvent>() {
            @Override
            public void onNext(HelloContract.HelloEvent helloEvent) {
                System.out.printf("Received HelloEvent, '%s' has been greeted.\n", helloEvent.getName());
                this.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                this.onComplete();
                throw new RuntimeException(e);
            }

            @Override
            public void onComplete() {
                if (!this.isDisposed()) {
                    this.dispose();
                }
                web3j.shutdown();
            }
        };
    }

    /**
     * Returns a factory producing instances of {@code Web3j}.
     * A {@code Web3j} object is used to interact with an Ethereum node, therefore
     * {@code EthereumContractWrapperGenerator} requires such factory in order to enable wrapper classes produced by it
     * to interact with the Ethereum blockchain.
     *
     * @return {@code Supplier} producing {@code Web3j} instances
     */
    private static Supplier<Web3j> getClientFactory(){
        web3j = new JsonRpc2_0Web3j(new HttpService("http://localhost:8545"));
        return () -> web3j;
    }

    /**
     * Returns a factory that produces a {@code TransactionManager} using the given {@code Web3j} instance.
     * The {@code TransactionManager} uses a prepared wallet file to sign transactions to be submitted to an Ethereum
     * network with the id 42.
     *
     * NOTE: Do NOT store passwords in source code for production use.
     *
     * @return {@code Function} that takes a {@code Web3j} and produces {@code TransactionManager}
     */
    private static Function<Web3j, TransactionManager> getTransactionManagerFactory(){
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials("Start123!", new File(WarmUp.class.getClassLoader().getResource("wallet.json").getFile()));
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }

        return (client) -> new RawTransactionManager(client, credentials, (byte)42);
    }

    /**
     * Returns a new instance of {@code TypeConverters}.
     * No additional {@code TypeConverter} are used in this example, therefore an empty instance is sufficient.
     *
     * @return {@code TypeConverters}
     */
    private static TypeConverters getTypeConverters(){
        return new TypeConverters();
    }
}

