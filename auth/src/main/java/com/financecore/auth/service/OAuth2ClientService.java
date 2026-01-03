package com.financecore.auth.service;

import com.financecore.auth.dto.request.ClientRegistrationRequest;
import com.financecore.auth.dto.response.ClientResponse;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

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

    /**
     * Get registered client from repository
     *
     * @param clientId client id
     * @return Registered client
     */
    RegisteredClient getRegisteredClient(String clientId);
}
