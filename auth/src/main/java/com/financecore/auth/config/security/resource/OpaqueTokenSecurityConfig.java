package com.financecore.auth.config.security.resource;

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
@Profile("oauth")
@Configuration
public class OpaqueTokenSecurityConfig {

    /**
     * Token introspector for auth service
     */
    private final LocalOpaqueTokenIntrospector localOpaqueTokenIntrospector;

    /**
     * Injecting required dependency through constructor injection
     */
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
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/register/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/register/admin").hasRole("ADMIN")
                        .requestMatchers("/v1/client/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(localOpaqueTokenIntrospector)));
        return httpSecurity.build();
    }
}

