package org.blockchainnative.fridge;

import org.blockchainnative.fabric.FabricUser;
import org.blockchainnative.fridge.blockchain.ReceiptContract;
import org.blockchainnative.fridge.items.Cheese;
import org.blockchainnative.fridge.items.Eggs;
import org.blockchainnative.fridge.items.Soda;
import org.blockchainnative.fridge.items.base.Item;
import org.blockchainnative.registry.ContractRegistry;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.Security;
import java.util.*;
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
    public void run(String... args) throws Exception {

        if (!receiptContract.getContractInfo().isInstantiated()) {
            LOGGER.info("Contract info states that ReceiptContract is not deployed. Deploying...");

            var peerOrg1 = "peer0.org1.example.com";
            var peerOrg2 = "peer0.org2.example.com";

            if (!receiptContract.getContractInfo().isInstalledOn(peerOrg1)) {
                LOGGER.info("Installing ReceiptContract on '{}'", peerOrg1);
                // TODO install the contract on peer0.org1.example.com with the correct user for organization 1 (see at the end of the class)
                // Use the install method on the contract wrapper
                receiptContract.install(getOrg1Admin(), new HashSet<>() {{ add(peerOrg1); }});

            }

            if (!receiptContract.getContractInfo().isInstalledOn(peerOrg2)) {
                LOGGER.info("Installing ReceiptContract on '{}'", peerOrg2);
                // TODO install the contract on peer0.org2.example.com with the correct user for organization 2 (see at the end of the class)
                // Use the install method on the contract wrapper
                receiptContract.install(getOrg2Admin(), new HashSet<>() {{ add(peerOrg2); }});

            }

            // TODO instantiate the contract using either the admin user of Org1 or Org2
            // Use the instantiate method on the contract wrapper
            LOGGER.info("Instantiating ReceiptContract ...");
            receiptContract.instantiate(getOrg1Admin());


            // TODO update the contract info in the contract registry
            LOGGER.info("Finished deployment, updating contract registry");
            contractRegistry.addOrUpdateContractInfo(receiptContract.getContractInfo());

        }

        var fridge = prepareRefrigerator(receiptContract);

        var hashes = Collections.synchronizedList(new ArrayList<>());
        // Registers an subscriber that adds the hash of incoming events to a list
        var disposable = receiptContract.onHashAdded()
                .subscribe(receiptAddedEvent -> hashes.add(receiptAddedEvent.sha256Hash));

        // TODO take enough items from the fridge so that the it needs to be restocked
        fridge.takeItem(Soda.class);
        fridge.takeItem(Soda.class);
        fridge.takeItem(Cheese.class);

        var receipt = fridge.restock();

        if (receipt.isPresent()) {
            // wait a few seconds until we are sure the event has been delivered
            Thread.sleep(5000);

            disposable.dispose();

            // TODO check if the receipts hash is present in the hash values obtained through the event subscriber
            // Note that you do not need to manually subscribe to the event, as the hash values contained in events
            // which were raised until now are already collected in the list 'hashes'
            var hash = receipt.get().getSHA256Hash();

            if (hashes.contains(hash)) {
                LOGGER.info("Receipt Hash found on blockchain!");
            } else {
                LOGGER.error("Receipt Hash not found on blockchain!");
            }
        } else {
            LOGGER.warn("Restocking was not necessary, check the refrigerators minimum supply.");
        }

        // TODO persist the contract info on the file system
        contractRegistry.persist();
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

    /**
     * Retrieves the admin user object for peers of organization 1. This peer is allowed to install chaincode on peers
     * of its organization.
     *
     * @return Org1 admin user
     * @throws IOException in case the certificate or private key file could not be loaded
     */
    private static User getOrg1Admin() throws IOException {
        return new FabricUser(
                "admin",
                "Org1MSP",
                FabricUser.createEnrollment(new ClassPathResource("user/admin@org1.example.com.pem").getFile(), new ClassPathResource("user/admin@org1.example.com.key").getFile()));
    }

    /**
     * Retrieves the admin user object for peers of organization 2. This peer is allowed to install chaincode on peers
     * of its organization.
     *
     * @return Org2 admin user
     * @throws IOException in case the certificate or private key file could not be loaded
     */
    private static User getOrg2Admin() throws IOException {
        return new FabricUser(
                "admin",
                "Org2MSP",
                FabricUser.createEnrollment(new ClassPathResource("user/admin@org2.example.com.pem").getFile(), new ClassPathResource("user/admin@org2.example.com.key").getFile()));
    }
}
