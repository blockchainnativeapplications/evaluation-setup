package org.blockchainnative.fridge;

import org.blockchainnative.fabric.Constants;
import org.blockchainnative.fabric.builder.FabricContractInfoBuilder;
import org.blockchainnative.fabric.metadata.ChaincodeLanguage;
import org.blockchainnative.fabric.metadata.FabricContractInfo;
import org.blockchainnative.fridge.blockchain.HashTypeConverter;
import org.blockchainnative.fridge.blockchain.ReceiptContract;
import org.blockchainnative.metadata.ContractInfo;
import org.blockchainnative.spring.autoconfigure.AbstractContractRegistry;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Configuration
public class ContractConfiguration {

    /**
     * Instructs the framework to pick up the type converter.
     *
     * @return type converter to be picked up
     */
    @Bean
    public HashTypeConverter hashTypeConverter() {
        return new HashTypeConverter();
    }

    /**
     * Instructs the framework to pick up the contract info objects.
     *
     * @return {@link AbstractContractRegistry} that registers contract info objects
     */
    @Bean
    public static BeanDefinitionRegistryPostProcessor ContractConfiguration() {
        return new AbstractContractRegistry() {
            @Override
            public Collection<? extends ContractInfo> getContractInfos() {
                var contractInfos = new ArrayList<FabricContractInfo<?>>();

                try {
                    // TODO complete the contract info
                    // - Set the chaincode source directory pointing to the ReceiptContract folder (make sure to use the full path)
                    // - Set the chaincode language to 'Go'
                    // - Register the instantiate method (Make sure to register its User parameter as special argument)
                    //   The documentation of FabricContractWrapperGenerator provides additional information about supported
                    //   special methods and arguments.

                    var receiptContractInfo = new FabricContractInfoBuilder<>(ReceiptContract.class)
                            .withIdentifier("receiptContract")
                            .withChainCodeIdentifier(ChaincodeID.newBuilder().setName("receiptContract").setVersion("1.0").setPath("receiptContract").build())
                            .withChaincodePolicy(getPolicy("ExamplePolicy.yaml"))
                            .installMethod("install", User.class, Set.class)
                                .parameter(0)
                                    .passAsSpecialArgWithName(Constants.USER_ARGUMENT)
                                    .build()
                                .parameter(1)
                                    .passAsSpecialArgWithName(Constants.TARGET_PEERS_ARGUMENT)
                                    .build()
                                .build()
                            .build();

                    contractInfos.add(receiptContractInfo);
                } catch (IOException e){
                    throw new RuntimeException(e);
                }

                return contractInfos;
            }

            private ChaincodeEndorsementPolicy getPolicy(String fileName) throws IOException {
                var policy = new ChaincodeEndorsementPolicy();

                try {
                    policy.fromYamlFile(new ClassPathResource(fileName).getFile().getAbsoluteFile());
                } catch (ChaincodeEndorsementPolicyParseException e) {
                    throw new RuntimeException(String.format("Failed to load chaincode endorsement policy from file '%s'", fileName), e);
                }

                return policy;
            }
        };
    }
}
