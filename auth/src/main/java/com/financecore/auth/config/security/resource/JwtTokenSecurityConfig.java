package com.financecore.auth.config.security.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Security configuration for jwt tokens
 *
 * @author Roshan
 */
@Profile("jwt")
@Configuration
public class JwtTokenSecurityConfig {

    /**
     * Injecting issuer url value through properties file
     */
    @Value("${fc.auth.jwt.secret}")
    private String jwtSecret;

    /**
     * Security filter chain for registering/retrieving clients info
     */
    @Bean
    @Order(3)
    public SecurityFilterChain jwtTokenFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/v1/**")
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/v1/register/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/register/admin").hasRole("ADMIN")
                        .requestMatchers("/v1/client/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtConverter())));
        return httpSecurity.build();
    }


    /**
     * Convert it to jwt authentication token
     */
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtConverter() {
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
     * Jwt decoder for decoding and validating token
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
