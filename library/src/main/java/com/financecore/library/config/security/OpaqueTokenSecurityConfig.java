package com.financecore.library.config.security;

import com.financecore.library.constants.SpringProfiles;
import com.financecore.library.properties.AuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Profile(SpringProfiles.OAUTH)
@Configuration
@ConditionalOnProperty(prefix = "fc.config", name = "security-resource", havingValue = "true", matchIfMissing = true)
public class OpaqueTokenSecurityConfig {

    private final AuthProperties authProperties;

    public OpaqueTokenSecurityConfig(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }


    /**
     * SecurityFilterChain for opaque token
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
    public OpaqueTokenIntrospector tokenIntrospector(){
        String introspectionUrl = authProperties.url() + "/oauth2/introspect";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(authProperties.client().id(), authProperties.client().secret()));
        return new SpringOpaqueTokenIntrospector(introspectionUrl, restTemplate);
    }
}
