package com.financecore.library.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Configuration class for adding Authorization token into feign request.
 * For intercommunication between services
 *
 * @author Roshan
 */
@Slf4j
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    /**
     * Responsible for managing token
     */
    private final OAuth2AuthorizedClientManager clientManager;

    /**
     * For which request is made(Other service)
     */
    private final String clientRegistrationId;

    /**
     * From which the request is made(Self service)
     */
    private final String principalName;


    /**
     * Create a dedicated interceptor to add bearer token in feign client outgoing request
     *
     * @param clientManager responsible for managing token
     * @param clientRegistrationId client in which request is made
     * @param principalName from which request is sent
     */
    public OAuth2FeignRequestInterceptor(OAuth2AuthorizedClientManager clientManager, String clientRegistrationId, String principalName) {
        this.clientManager = clientManager;
        this.clientRegistrationId = clientRegistrationId;
        this.principalName = principalName;
    }


    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     *
     * @param template template
     */
    @Override
    public void apply(RequestTemplate template) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistrationId)
                .principal(principalName)
                .build();

        OAuth2AuthorizedClient authorizedClient = clientManager.authorize(authorizeRequest);

        if (authorizedClient != null){
            String tokenValue = authorizedClient.getAccessToken().getTokenValue();
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
            log.debug("Added OAuth2 Bearer token for service {} with principal {}", clientRegistrationId, principalName);
        } else {
            log.error("Failed to obtain Bearer token for service {} with principal {}", clientRegistrationId, principalName);
        }

    }
}
