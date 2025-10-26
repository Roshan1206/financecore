package com.financecore.account.config.feign;

import com.financecore.account.config.oauth2.OAuth2FeignRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Feign configuration for transaction service.
 *
 * @author Roshan
 */
@Configuration
public class TransactionServiceFeignConfig {

    /**
     * Creates OAuth2 interceptor specifically for Transaction Service calls.
     * Uses "transaction-service" client registration from application.yml.
     *
     * @param clientManager - Manages OAuth2 token lifecycle
     * @param serviceName - Current service name (e.g., "account") used as principal
     * @return Configured interceptor for transaction service authentication
     */
    @Bean
    public OAuth2FeignRequestInterceptor transactionServiceInterceptor(OAuth2AuthorizedClientManager clientManager,
                                                                       @Value("spring.application.name") String serviceName) {
        return new OAuth2FeignRequestInterceptor(clientManager, "transaction-service", serviceName);
    }
}
