package com.financecore.transaction.service.communication;

import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.http.ResponseEntity;

/**
 * Communication client for cross communication.
 * To be implemented with both feign and web
 *
 * @author Roshan
 */
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
