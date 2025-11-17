package com.financecore.transaction.config.security;

import com.financecore.transaction.constants.Constant;
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
 * Configuration class for opaque token
 *
 * @author Roshan
 */
@Profile(Constant.PROFILE_OAUTH)
@Configuration
public class OpaqueTokenSecurityConfig {

    /**
     * Injecting auth server url
     */
    @Value("${transaction.config.auth.url}")
    private String authUrl;

    /**
     * Injecting client id
     */
    @Value("${transaction.config.auth.client.id}")
    private String clientId;

    /**
     * Injecting client secret
     */
    @Value("${transaction.config.auth.client.secret}")
    private String clientSecret;


    /**
     * Filter chain for validating opaque token
     */
    @Order(2)
    @Bean
    public SecurityFilterChain opaqueTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(tokenIntrospector())));
        return httpSecurity.build();
    }


    /**
     * Introspector for communicating and validating token with auth server
     */
    @Bean
    public OpaqueTokenIntrospector tokenIntrospector() {
        String introspectionUrl = authUrl + "/oauth2/introspect";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
        return new SpringOpaqueTokenIntrospector(introspectionUrl, restTemplate);
    }
}
