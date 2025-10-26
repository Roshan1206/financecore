package com.financecore.auth.service;

import com.financecore.auth.dto.request.ClientRegistrationRequest;
import com.financecore.auth.dto.response.ClientResponse;

/**
 * Interface for managing OAuth2 clients.
 *
 * @author Roshan
 */
public interface OAuth2ClientService {

    /**
     * Register new client
     *
     * @param clientRegistrationRequest Client info
     */
    ClientResponse registerClient(ClientRegistrationRequest clientRegistrationRequest);
}
