package com.financecore.account.service.communication;

/**
 * Communication client for cross communication.
 * To be implemented with both feign and web
 *
 * @author Roshan
 */
public interface CommunicationClient {

    /**
     * Validate customer number from customer service and retrieve customer id
     *
     * @param customerNumber customer number
     * @return customer id
     */
    String getAndValidateCustomer(long customerNumber);
}
