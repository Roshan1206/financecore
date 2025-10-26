package com.financecore.auth.config.security.client;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
@Configuration
public class ActuatorSecurityConfiguration {

    /**
     * Injecting SecurityProperties to extract username and password
     */
    private final SecurityProperties securityProperties;


    /**
     * Injecting required dependency via Constructor injection.
     */
    public ActuatorSecurityConfiguration(SecurityProperties securityProperties){
        this.securityProperties = securityProperties;
    }


    /**
     * Actuator filter chain for customizing actuator endpoints
     */
    @Bean
    @Order(2)
    public SecurityFilterChain actuatorFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests(req -> req
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class), EndpointRequest.to(InfoEndpoint.class)).permitAll()
                        .anyRequest().hasRole("ACTUATOR"))
                .authenticationProvider(new DaoAuthenticationProvider(actuatorUser()))
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }


    /**
     * Defining custom user details for actuator endpoints
     */
    public UserDetailsService actuatorUser() {
        UserDetails userDetails = User
                .withUsername(securityProperties.getUser().getName())
                .password("{noop}" + securityProperties.getUser().getPassword())
                .roles("ACTUATOR")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }
}
