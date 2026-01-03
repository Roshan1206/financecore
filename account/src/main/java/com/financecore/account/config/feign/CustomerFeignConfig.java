package com.financecore.account.config.feign;

import com.financecore.library.interceptor.OAuth2FeignRequestInterceptor;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Creates interceptor for feign client to add authorization header
 */
@Profile(SpringProfiles.FEIGN)
@Configuration
public class CustomerFeignConfig {

    /**
     * Creates OAuth2 interceptor specifically for Customer Service calls.
     * Uses "customer-service" client registration from application.yml.
     *
     * @param clientManager - Manages OAuth2 token lifecycle
     * @param serviceName - Current service name (e.g., "account") used as principal
     * @return Configured interceptor for customer service authentication
     */
    @Bean
    public OAuth2FeignRequestInterceptor customerServiceInterceptor(OAuth2AuthorizedClientManager clientManager,
                                                                       @Value("spring.application.name") String serviceName) {
        return new OAuth2FeignRequestInterceptor(clientManager, "customer-service", serviceName);
    }
}
