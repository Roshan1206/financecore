package com.financecore.transaction.service.communication.impl;

import com.financecore.transaction.service.communication.AccountFeignClient;
import com.financecore.transaction.service.communication.CommunicationClient;
import com.financecore.transaction.constants.Constant;
import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Used for communication to accounts service using feign client.
 *
 * @author Roshan
 */
@Profile(Constant.PROFILE_FEIGN)
@Component
public class AccountFeignCommunication implements CommunicationClient {

    /**
     * Feign client interface for account
     */
    private final AccountFeignClient accountFeignClient;

    /**
     * Injecting dependency using constructor injection
     */
    public AccountFeignCommunication(AccountFeignClient accountFeignClient) {
        this.accountFeignClient = accountFeignClient;
    }


    /**
     * Get account balance
     *
     * @param accountNumber customer account number
     * @return balance from accounts service
     */
    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(String accountNumber) {
        return accountFeignClient.getAccountBalance(accountNumber);
    }

    /**
     * Update customer account balance
     *
     * @param accountNumber customer account number
     * @param updateAccountRequest dto class
     */
    @Override
    public void updateAccountBalance(String accountNumber, UpdateAccountRequest updateAccountRequest) {
        accountFeignClient.updateAccountBalance(accountNumber, updateAccountRequest);
    }

    /**
     * Validate if account exist and is in ACTIVE state
     *
     * @param accountNumber customer account number
     * @return true/false based on validation
     */
    @Override
    public boolean validateAccount(String accountNumber) {
        return accountFeignClient.validateAccount(accountNumber);
    }
}
