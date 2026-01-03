package com.financecore.account.service.communication.impl;

import com.financecore.account.service.communication.CommunicationClient;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Communication to customer service using web client
 *
 * @author Roshan
 */
@Profile(SpringProfiles.WEB)
@Component
public class CustomerWebCommunication implements CommunicationClient {


    /**
     * creating web client for customer service.
     */
    private final WebClient client;

    /**
     * Injecting required dependency through constructor injection
     */
    public CustomerWebCommunication(@Qualifier("customerClient") WebClient client) {
        this.client = client;
    }

    /**
     * Validate customer number from customer service and retrieve customer id
     *
     * @param customerNumber customer number
     * @return customer id
     */
    @Override
    public String getAndValidateCustomer(long customerNumber) {
        return client.post()
                .uri("/{customerNumber}/validate", customerNumber)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")))
                .onStatus(HttpStatusCode::is5xxServerError, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")))
                .bodyToMono(String.class)
                .switchIfEmpty(Mono.error(new IllegalStateException("Invalid customer id")))
                .block();
    }
}
