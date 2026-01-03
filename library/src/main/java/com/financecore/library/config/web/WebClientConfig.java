package com.financecore.library.config.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Base class for creating web client for intercommunication between services.
 *
 * @author Roshan
 */
public class WebClientConfig {

    /**
     * builder for creating client
     */
    private final WebClient.Builder builder;

    /**
     * for extracting and adding header in outgoing request
     */
    private final HttpServletRequest request;

    /**
     * Injecting required dependency through constructor injection
     */
    public WebClientConfig(WebClient.Builder builder, HttpServletRequest request) {
        this.builder = builder;
        this.request = request;
    }

    /**
     * create WebClient bean object for service
     */
    public WebClient create(String baseUrl) {
        return builder
                .baseUrl(baseUrl + "/api/v1")
                .filter((req, next) -> {
                    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                    if (token != null){
                        req = ClientRequest.from(req).header(HttpHeaders.AUTHORIZATION, token).build();
                    }
                    return next.exchange(req);
                })
                .build();
    }
}
