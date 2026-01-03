package com.financecore.account.service.communication.impl;

import com.financecore.account.service.communication.CommunicationClient;
import com.financecore.account.service.communication.CustomerFeignClient;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation class for {@link CommunicationClient} when feign profile is active
 *
 * @author Roshan
 */
@Profile(SpringProfiles.FEIGN)
@Component
public class CustomerFeignCommunication implements CommunicationClient {

    /**
     * Customer feign client
     */
    private final CustomerFeignClient client;

    /**
     * Injecting required dependency through constructor injection
     */
    public CustomerFeignCommunication(CustomerFeignClient client) {
        this.client = client;
    }


    /**
     * Validate customer number from customer service and retrieve customer id
     *
     * @param customerNumber customer number
     * @return customer id
     */
    @Override
    public String getAndValidateCustomer(long customerNumber) {
        return client.validateCustomer(customerNumber);
    }
}
