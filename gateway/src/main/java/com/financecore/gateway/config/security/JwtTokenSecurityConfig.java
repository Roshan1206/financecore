package com.financecore.gateway.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

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
    @Value("${gateway.config.auth.url}")
    private String authUrl;


    /**
     * Filter chain for validating jwt tokens
     */
    @Bean
    public SecurityWebFilterChain jwtTokenFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter())
                                .jwtDecoder(jwtDecoder())));
        return httpSecurity.build();
    }


    /**
     * Creating jwt decoder with token validator
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder(){
        NimbusReactiveJwtDecoder decoder = ReactiveJwtDecoders.fromIssuerLocation(authUrl);
        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(authUrl);
        decoder.setJwtValidator(validator);
        return decoder;
    }


    /**
     * Converting roles and scopes claim for validating access
     */
    @Bean
    public Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> jwtConverter() {
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
            return Mono.just(new JwtAuthenticationToken(jwt, combined, jwt.getSubject()));
        };
    }
}
