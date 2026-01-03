package com.financecore.customer.service.communication;

import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.PageResponse;
import org.springframework.http.ResponseEntity;

/**
 * Communication client for cross communication.
 * To be implemented with both feign and web
 *
 * @author Roshan
 */
public interface CommunicationClient {

    /**
     * Get all accounts for any customer
     */
    ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(String customerId);
}
