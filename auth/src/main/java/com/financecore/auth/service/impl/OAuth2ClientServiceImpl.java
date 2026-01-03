package com.financecore.auth.service.impl;

import com.financecore.auth.dto.request.ClientRegistrationRequest;
import com.financecore.auth.dto.response.ClientResponse;
import com.financecore.auth.service.OAuth2ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;

/**
 * Service class for managing OAuth2 clients.
 *
 * @author Roshan
 */
@Slf4j
@Service
public class OAuth2ClientServiceImpl implements OAuth2ClientService {

    /**
     * Encoder for client secret.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repository responsible for managing clients.
     */
    private final RegisteredClientRepository registeredClientRepository;


    /**
     * Injecting required dependency via constructor injection.
     */
    public OAuth2ClientServiceImpl(PasswordEncoder passwordEncoder, RegisteredClientRepository registeredClientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.registeredClientRepository = registeredClientRepository;
    }


    /**
     * Register new client
     *
     * @param clientRegistrationRequest Client info
     */
    @Override
    public ClientResponse registerClient(ClientRegistrationRequest clientRegistrationRequest) {
        RegisteredClient registeredClient = getRegisteredClient(clientRegistrationRequest.getClientId());
        if (registeredClient != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client Id already exists");
        }

        OAuth2TokenFormat tokenFormat = "jwt".equalsIgnoreCase(clientRegistrationRequest.getTokenFormat())
                ? OAuth2TokenFormat.SELF_CONTAINED
                : OAuth2TokenFormat.REFERENCE;

        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenFormat(tokenFormat)
                .reuseRefreshTokens(true)
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofDays(30))
                .build();

        RegisteredClient.Builder clientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientRegistrationRequest.getClientId())
                .clientName(clientRegistrationRequest.getClientName())
                .clientSecret(passwordEncoder.encode(clientRegistrationRequest.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(tokenSettings);

        if (clientRegistrationRequest.getGrantTypes() != null) {
            clientRegistrationRequest.getGrantTypes().forEach(grantType -> {
                switch (grantType.toLowerCase()) {
                    case "authorization_code" -> clientBuilder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
                    case "client_credentials" -> clientBuilder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    case "refresh_token" -> clientBuilder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
                }
            });
        }

        if (clientRegistrationRequest.getScopes() != null) {
            clientRegistrationRequest.getScopes().forEach(clientBuilder::scope);
        }

        if (clientRegistrationRequest.getRedirectUris() != null) {
            clientRegistrationRequest.getRedirectUris().forEach(clientBuilder::redirectUri);
        } else {
            clientBuilder.redirectUri("https://oauth.pstmn.io/v1/callback");
        }

        RegisteredClient client = clientBuilder.build();
        registeredClientRepository.save(client);
        return createClientResponse(client);
    }

    /**
     * Get registered client from repository
     *
     * @param clientId client id
     * @return Registered client
     */
    @Override
    public RegisteredClient getRegisteredClient(String clientId) {
        return registeredClientRepository.findByClientId(clientId);
    }


    /**
     * Create client response for user
     *
     * @param client Registered client
     */
    private ClientResponse createClientResponse(RegisteredClient client) {
        ClientResponse response = new ClientResponse();
        response.setClientId(client.getClientId());
        response.setClientName(client.getClientName());
        response.setTokenFormat(client.getTokenSettings().getAccessTokenFormat().getValue());
        response.setRedirectUris(client.getRedirectUris().stream().toList());
        response.setScopes(client.getScopes().stream().toList());
        response.setGrantTypes(client.getAuthorizationGrantTypes().stream().map(AuthorizationGrantType::getValue).toList());
        return response;
    }
}
