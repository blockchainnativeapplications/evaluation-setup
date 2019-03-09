package org.blockchainnative.fridge;

import org.blockchainnative.ethereum.builder.EthereumContractInfoBuilder;
import org.blockchainnative.ethereum.metadata.EthereumContractInfo;
import org.blockchainnative.fridge.blockchain.HashTypeConverter;
import org.blockchainnative.fridge.blockchain.ReceiptContract;
import org.blockchainnative.metadata.ContractInfo;
import org.blockchainnative.spring.autoconfigure.AbstractContractRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Matthias Veit
 */
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
                var contractInfos = new ArrayList<EthereumContractInfo<?>>();

                try {
                    // TODO add a contract info object for ReceiptContract
                    contractInfos.add(
                            new EthereumContractInfoBuilder<>(ReceiptContract.class)
                                    .withIdentifier("receiptContract")
                                    .withAbi(new ClassPathResource("ReceiptContract/ReceiptContract.abi").getFile())
                                    .withBinary(new ClassPathResource("ReceiptContract/ReceiptContract.bin").getFile())
                            .build()
                    );
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
                return contractInfos;
            }
        };
    }
}
