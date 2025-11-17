package com.financecore.transaction.client;

import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.http.ResponseEntity;

public interface CommunicationClient {

    /**
     * Get account balance
     *
     * @param accountNumber customer account number
     */
    ResponseEntity<BalanceResponse> getAccountBalance(String accountNumber);


    /**
     * Update customer account balance
     *
     * @param accountNumber customer account number
     * @param updateAccountRequest request dto
     */
    void updateAccountBalance(String accountNumber,
                              UpdateAccountRequest updateAccountRequest);

    /**
     * Validate if account exist and is in ACTIVE state
     *
     * @param accountNumber customer account number
     */
    boolean validateAccount(String accountNumber);
}
