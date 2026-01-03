package com.financecore.account.config.web;

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
     * Injecting transaction service url
     */
    @Value("${fc.config.transaction.url}")
    private String transactionUrl;

    /**
     * Injecting customer service url
     */
    @Value("${fc.config.customer.url")
    private String accountUrl;


    /**
     * creating web client for transaction service.
     */
    @Bean("transactionClient")
    public WebClient transactionClient(WebClientConfig webClientConfig) {
        return webClientConfig.create(transactionUrl + "/v1");
    }


    /**
     * creating web client for customer service.
     */
    @Bean("customerClient")
    public WebClient customerClient(WebClientConfig webClientConfig) {
        return webClientConfig.create(accountUrl + "/v1");
    }
}
