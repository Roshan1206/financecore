package com.financecore.library.config.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for actuator endpoints
 *
 * @author Roshan
 */
@AutoConfiguration
public class ActuatorSecurityConfig {

    /**
     * Injecting SecurityProperties to extract username and password
     */
    private final SecurityProperties securityProperties;


    /**
     * Injecting required dependency via Constructor injection.
     */
    public ActuatorSecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }


    /**
     * Actuator filter chain for customizing actuator endpoints
     */
    @Order(1)
    @Bean
    public SecurityFilterChain actuatorFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests(req -> req
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class), EndpointRequest.to(InfoEndpoint.class)).permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(new DaoAuthenticationProvider(userDetails()))
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }


    /**
     * Defining custom user details for actuator endpoints
     */
    public UserDetailsService userDetails() {
        UserDetails userDetails = User
                .withUsername(securityProperties.getUser().getName())
                .password("{noop}" + securityProperties.getUser().getPassword())
                .roles("ACTUATOR")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
