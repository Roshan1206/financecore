package com.financecore.gateway.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Opaque token security configuration.
 *
 * @author Roshan
 */
@Profile("!jwt")
@Configuration
public class OpaqueTokenSecurityConfig {

    /**
     * Injecting auth server url
     */
    @Value("${gateway.config.auth.url}")
    private String authUrl;

    /**
     * Injecting client id
     */
    @Value("${gateway.config.auth.client.id}")
    private String clientId;

    /**
     * Injecting client secret
     */
    @Value("${gateway.config.auth.client.secret}")
    private String clientSecret;

    /**
     * Filter chain for validating opaque token
     */
    @Bean
    public SecurityWebFilterChain opaqueTokenFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(tokenIntrospector())));
        return httpSecurity.build();
    }


    /**
     * TokenIntrospector for validating token.
     */
    @Bean
    public ReactiveOpaqueTokenIntrospector tokenIntrospector() {
        String introspectionUrl = authUrl + "/oauth2/introspect";
        return SpringReactiveOpaqueTokenIntrospector
                .withIntrospectionUri(introspectionUrl)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
