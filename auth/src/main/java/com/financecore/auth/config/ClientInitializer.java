package com.financecore.auth.config;

import com.financecore.auth.dto.request.ClientRegistrationRequest;
import com.financecore.auth.service.OAuth2ClientService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initialize oidc client if not present.
 *
 * @author Roshan
 */
@Component
public class ClientInitializer {

    /**
     * Service class for registering client.
     */
    private final OAuth2ClientService oAuth2ClientService;


    /**
     * Injecting required dependency via constructor injection
     */
    public ClientInitializer(OAuth2ClientService oAuth2ClientService) {
        this.oAuth2ClientService = oAuth2ClientService;
    }


    /**
     * Register OIDC client for managing user and client if not present.
     * Invoked once after application starts.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeClient() {
        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest();
        clientRegistrationRequest.setClientId("user-service");
        clientRegistrationRequest.setClientName("user-service");
        clientRegistrationRequest.setClientSecret("user-secret");
        clientRegistrationRequest.setGrantTypes(List.of("client_credentials", "authorization_code", "refresh_token"));
        clientRegistrationRequest.setScopes(List.of("read", "write"));
        clientRegistrationRequest.setTokenFormat("opaque");

        oAuth2ClientService.registerClient(clientRegistrationRequest);
    }
}
