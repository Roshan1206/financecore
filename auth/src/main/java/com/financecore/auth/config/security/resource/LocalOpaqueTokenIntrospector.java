package com.financecore.auth.config.security.resource;

import com.financecore.auth.entity.User;
import com.financecore.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Custom introspector for local token validation
 *
 * @author Roshan
 */
@Component
public class LocalOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    /**
     * Responsible for authorization management.
     */
    private final OAuth2AuthorizationService authorizationService;

    /**
     * Repository responsible for managing users.
     */
    private final UserRepository userRepository;


    /**
     * Injecting required dependency via constructor injection.
     */
    public LocalOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService, UserRepository userRepository) {
        this.authorizationService = authorizationService;
        this.userRepository = userRepository;
    }


    @Override
    public OAuth2IntrospectionAuthenticatedPrincipal introspect(String token) {
        // Find the authorization by token value
        OAuth2Authorization authorization = authorizationService.findByToken(
                token,
                OAuth2TokenType.ACCESS_TOKEN
        );

        if (authorization == null) {
            throw new BadOpaqueTokenException("Invalid token");
        }

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
            throw new BadOpaqueTokenException("Token expired");
        }

        // Build the authenticated principal
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", authorization.getPrincipalName());
        attributes.put("scope", authorization.getAuthorizedScopes());
        attributes.put("client_id", authorization.getRegisteredClientId());
        attributes.put("token_type", "Bearer");
        attributes.put("active", true);

        return new OAuth2IntrospectionAuthenticatedPrincipal(
                authorization.getPrincipalName(),
                attributes,
                extractAuthorities(authorization.getPrincipalName())
        );
    }

    private Collection<GrantedAuthority> extractAuthorities(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        return new HashSet<>(user.getAuthorities());
    }
}