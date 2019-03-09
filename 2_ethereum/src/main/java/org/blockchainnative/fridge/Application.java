package org.blockchainnative.fridge;

import org.blockchainnative.fridge.blockchain.ReceiptContract;
import org.blockchainnative.fridge.items.Cheese;
import org.blockchainnative.fridge.items.Eggs;
import org.blockchainnative.fridge.items.Soda;
import org.blockchainnative.fridge.items.base.Item;
import org.blockchainnative.registry.ContractRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Example application demonstrating the framework blockchain-native applications.
 *
 * @author Matthias Veit
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /**
     * A field annotated with @Autowired is automatically injected by Spring. The framework takes care that for each
     * registered contract info, a smart contract wrapper is generated. If multiple instances of the same smart contract
     * interface are needed, the annotation @Qualifier is required to specify the contract identifier.
     */
    @Autowired
    private ReceiptContract receiptContract;

    /**
     * The framework automatically instantiates a {@link ContractRegistry}, more precisely {@link
     * org.blockchainnative.registry.FileSystemContractRegistry} targeting the 'contracts' folder in the project
     * directory. Each contract info in this folder is automatically loaded at application startup. Manually registered
     * contract info objects are only added if there was none with the same identifier on the file system.
     * <p>
     * NOTE: In case you executed the program before but restarted the docker network, make sure to remove the contract
     * info object from this as the assumed and actual network state won't match up anymore.
     */
    @Autowired
    private ContractRegistry contractRegistry;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        // TODO make sure, that the injected ReceiptContract has been deployed and update the contractRegistry
        if (!receiptContract.getContractInfo().isDeployed()) {
            LOGGER.info("Contract info states that ReceiptContract is not deployed. Deploying...");
            receiptContract.deploy();

            LOGGER.info("Finished deployment, updating contract registry");
            contractRegistry.addOrUpdateContractInfo(receiptContract.getContractInfo());
        }

        var fridge = prepareRefrigerator(receiptContract);

        // TODO take enough items from the fridge so that it needs to be restocked
        // If it contains less items than specified in its minimum supply, restock will place an order
        fridge.takeItem(Soda.class);
        fridge.takeItem(Soda.class);
        fridge.takeItem(Cheese.class);

        var receipt = fridge.restock();

        if (receipt.isPresent()) {
            // TODO make sure the hash value is present in the receipt hashes stored in the smart contract
            var hash = receipt.get().getSHA256Hash();


            if (receiptContract.getReceiptHashes().contains(hash)) {
                LOGGER.info("Receipt Hash found on blockchain!");
            } else {
                LOGGER.error("Receipt Hash not found on blockchain!");
            }
        } else {
            LOGGER.warn("Restocking was not necessary, check the refrigerators minimum supply.");
        }

        // TODO persist the contract info on the file system
        try {
            contractRegistry.persist();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a {@code Refrigerator} using the given {@code ReceiptContract}.
     * <p>
     * Moreover it sets its minimum supply and initially restocks the refrigerator.
     *
     * @param receiptContract
     * @return refrigerator initialized with minimum supply
     */
    private static Refrigerator prepareRefrigerator(ReceiptContract receiptContract) {
        var orderService = new OrderServiceImpl(receiptContract);
        var fridge = new Refrigerator(orderService);

        var minimumSupply = new HashMap<Class<? extends Item>, Integer>();
        minimumSupply.put(Cheese.class, 1);
        minimumSupply.put(Eggs.class, 1);
        minimumSupply.put(Soda.class, 4);

        // Set the minimum amount of packages per type
        // If the contents of the fridge do not cover the minimum supply, restocking is required
        fridge.setMinimumSupply(minimumSupply);

        // Order the minimum supply
        fridge.restock();

        LOGGER.info("Prepared refrigerator with the following items: {}", fridge.getContents().entrySet().stream()
                .map(entry -> String.format("%dx'%s' (%s)", entry.getValue(), entry.getKey().getItemId(), entry.getKey().getClass().getSimpleName()))
                .collect(Collectors.joining(", ")));

        return fridge;
    }
}
