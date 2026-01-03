package com.financecore.customer.service.communication.impl;

import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.service.communication.CommunicationClient;
import com.financecore.library.constants.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Inter communication between services using web client
 *
 * @author Roshan
 */
@Profile(SpringProfiles.WEB)
@Component
public class AccountWebClientImpl implements CommunicationClient {

    /**
     * web client for rest calls
     */
    private final WebClient webClient;

    /**
     * Injecting required dependency using constructor injection
     */
    public AccountWebClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Get all accounts for any customer
     *
     * @param customerId customer id
     * @return accounts page
     */
    @Override
    public ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(String customerId) {
        return webClient
                .get()
                .uri("/customer/" + customerId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        res -> Mono.error(new ResponseStatusException(res.statusCode(), "Internal Server error")))
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<PageResponse<AccountsResponse>>>() {})
                .retry(3)
                .block();
    }
}
