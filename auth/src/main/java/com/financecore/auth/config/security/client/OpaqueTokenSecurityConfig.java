package com.financecore.auth.config.security.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Opaque token security configuration.
 *
 * @author Roshan
 */
@Profile("!jwt")
@Configuration
public class OpaqueTokenSecurityConfig {

    private final LocalOpaqueTokenIntrospector localOpaqueTokenIntrospector;

    /**
     * Injecting auth server url
     */
    @Value("${auth.config.auth.url}")
    private String authUrl;

    /**
     * Injecting client id
     */
    @Value("${auth.config.auth.client.id}")
    private String clientId;

    /**
     * Injecting client secret
     */
    @Value("${auth.config.auth.client.secret}")
    private String clientSecret;


    public OpaqueTokenSecurityConfig(LocalOpaqueTokenIntrospector localOpaqueTokenIntrospector) {
        this.localOpaqueTokenIntrospector = localOpaqueTokenIntrospector;
    }


    /**
     * SecurityFilterChain for opaque token
     */
    @Order(3)
    @Bean
    public SecurityFilterChain opaqueTokenFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/v1/**")
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, "/v1/auth/**").permitAll()
                        .requestMatchers("/v1/client/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(localOpaqueTokenIntrospector)));
        return httpSecurity.build();
    }
}

