package com.financecore.customer.config.feign;

import com.financecore.customer.config.oauth2.OAuth2FeignRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Feign configuration for Account service.
 *
 * @author Roshan
 */
@Configuration
public class AccountServiceFeignConfig {

    /**
     * Creates OAuth2 interceptor specifically for Account Service calls.
     *
     * @param clientManager - Manages OAuth2 token lifecycle
     * @param serviceName - Current service name used as principal
     * @return Configured interceptor for account service authentication
     */
    @Bean
    public OAuth2FeignRequestInterceptor accountServiceInterceptor(OAuth2AuthorizedClientManager clientManager,
                                                                       @Value("${spring.application.name}") String serviceName) {
        return new OAuth2FeignRequestInterceptor(clientManager, "account-service", serviceName);
    }
}
