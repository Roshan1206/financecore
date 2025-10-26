package com.financecore.customer.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

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
    @Value("${customer.config.auth.url}")
    private String authUrl;

    /**
     * Injecting client id
     */
    @Value("${customer.config.auth.client.id}")
    private String clientId;

    /**
     * Injecting client secret
     */
    @Value("${customer.config.auth.client.secret}")
    private String clientSecret;


    /**
     * Filter chain for validating opaque token
     */
    @Order(2)
    @Bean
    public SecurityFilterChain opaqueTokenFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(tokenIntrospector())));
        return httpSecurity.build();
    }


    /**
     * TokenIntrospector for validating token.
     */
    @Bean
    public OpaqueTokenIntrospector tokenIntrospector() {
        String introspectionUrl = authUrl + "/oauth2/introspect";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
        return new SpringOpaqueTokenIntrospector(introspectionUrl, restTemplate);
    }
}
