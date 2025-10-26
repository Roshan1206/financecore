package com.financecore.transaction.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Jwt security configurations with decoder for validating token
 *
 * @author Roshan
 */
@Profile("jwt")
@Configuration
public class JwtTokenSecurityConfig {

    /**
     * Inject auth server url
     */
    @Value("${transaction.config.auth.url}")
    private String authUrl;


    /**
     * Filter chain for validating jwt tokens
     */
    @Order(2)
    @Bean
    public SecurityFilterChain jwtTokenFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(converter())
                                .decoder(jwtDecoder())));
        return httpSecurity.build();
    }


    /**
     * Converting roles and scopes claim for validating access
     */
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> converter() {
        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        scopeConverter.setAuthoritiesClaimName("scope");
        scopeConverter.setAuthorityPrefix("SCOPE_");

        JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();
        roleConverter.setAuthoritiesClaimName("role");
        roleConverter.setAuthorityPrefix("");

        return jwt -> {
            Set<GrantedAuthority> combined = new HashSet<>();
            Collection<GrantedAuthority> scopes = scopeConverter.convert(jwt);
            if (scopes != null) combined.addAll(scopes);
            Collection<GrantedAuthority> roles = roleConverter.convert(jwt);
            if (roles != null) combined.addAll(roles);
            return new JwtAuthenticationToken(jwt, combined, jwt.getSubject());
        };
    }


    /**
     * Creating jwt decoder with token validator
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(authUrl);
        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(authUrl);
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
