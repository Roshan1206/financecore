package com.financecore.customer.config.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("client")
@Configuration
public class WebClientConfig {

    @Value("${customer.config.accounts.url")
    private String accountUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder, HttpServletRequest request) {
        return builder
                .baseUrl(accountUrl + "/api/v1")
                .filter((req, next) -> {
                    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                    if (token != null) {
                        req = ClientRequest.from(req).header(HttpHeaders.AUTHORIZATION, token).build();
                    }
                    return next.exchange(req);
                }).build();
    }
}
