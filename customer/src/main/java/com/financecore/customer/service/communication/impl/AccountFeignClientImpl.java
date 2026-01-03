package com.financecore.customer.service.communication.impl;

import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.service.communication.AccountFeignClient;
import com.financecore.customer.service.communication.CommunicationClient;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Used for communication to accounts service using feign client.
 *
 * @author Roshan
 */
@Profile(SpringProfiles.FEIGN)
@Component
public class AccountFeignClientImpl implements CommunicationClient {

    /**
     * Feign client interface for account
     */
    private final AccountFeignClient feignClient;

    /**
     * Injecting dependency using constructor injection
     */
    public AccountFeignClientImpl(AccountFeignClient feignClient) {
        this.feignClient = feignClient;
    }


    /**
     * Get all accounts for any customer
     *
     * @param customerId customer id
     */
    @Override
    public ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(String customerId) {
        return feignClient.getCustomerAccounts(customerId);
    }
}
