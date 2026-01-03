package com.financecore.customer.config.web;

import com.financecore.library.config.web.WebClientConfig;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for creating communication for web profile
 *
 * @author Roshan
 */
@Profile(SpringProfiles.WEB)
@Configuration
public class AccountWebClientConfig {

    /**
     * Injecting account service url
     */
    @Value("${fc.config.accounts.url")
    private String accountUrl;


    /**
     * creating web client for transaction service.
     */
    @Bean
    public WebClient accountClient(WebClientConfig webClientConfig) {
        return webClientConfig.create(accountUrl);
    }
}
