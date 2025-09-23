package com.financecore.auth.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AuthSecurityConfiguration {

    /**
     * Injecting issuer url value through properties file
     */
    @Value("${auth.config.issuer.url}")
    private String issuerUrl;

    /**
     * Security filter chain for registering/retrieving clients info
     */
    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }


    /**
     * Jwt decoder
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource){
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    /**
     * Creating Providers for JWT and Opaque
     */
    @Bean
    public AuthenticationManager authenticationManager(JWKSource<SecurityContext> jwkSource){
        JwtDecoder jwtDecoder = jwtDecoder(jwkSource);
        AuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("finance-core", "finance-core-secret"));
        SpringOpaqueTokenIntrospector springOpaqueTokenIntrospector = new SpringOpaqueTokenIntrospector(issuerUrl + "/oauth2/introspection", restTemplate);
        AuthenticationProvider opaqueProvider = new OpaqueTokenAuthenticationProvider(springOpaqueTokenIntrospector);
        return new ProviderManager(List.of(jwtProvider, opaqueProvider));
    }
}
