package com.financecore.transaction.client.impl;

import com.financecore.transaction.client.AccountFeignClient;
import com.financecore.transaction.client.CommunicationClient;
import com.financecore.transaction.constants.Constant;
import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Profile(Constant.PROFILE_FEIGN)
@Component
public class AccountFeignCommunication implements CommunicationClient {

    private final AccountFeignClient accountFeignClient;

    public AccountFeignCommunication(AccountFeignClient accountFeignClient) {
        this.accountFeignClient = accountFeignClient;
    }


    /**
     * Get account balance
     *
     * @param accountNumber customer account number
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
     */
    @Override
    public boolean validateAccount(String accountNumber) {
        return accountFeignClient.validateAccount(accountNumber);
    }
}
