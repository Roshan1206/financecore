package com.financecore.transaction.service.communication.impl;

import com.financecore.transaction.service.communication.CommunicationClient;
import com.financecore.transaction.constants.Constant;
import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Intercommunication between services using web client
 *
 * @author Roshan
 */
@Profile(Constant.PROFILE_WEB)
@Component
public class AccountWebCommunication implements CommunicationClient {

    /**
     * For making rest calls
     */
    private final WebClient webClient;

    /**
     * Injecting required dependency using constructor injection
     */
    public AccountWebCommunication(WebClient webClient) {
        this.webClient = webClient;
    }


    /**
     * Get account balance
     *
     * @param accountNumber customer account number
     */
    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(String accountNumber) {
        return webClient
                .get()
                .uri("/" + accountNumber + "/balance")
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        res -> Mono.error(new ResponseStatusException(res.statusCode(), "Something bad happened...")))
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<BalanceResponse>>() {
                })
                .retry(3)
                .block();
    }


    /**
     * Update customer account balance
     *
     * @param accountNumber        customer account number
     * @param updateAccountRequest request dto
     */
    @Override
    public void updateAccountBalance(String accountNumber, UpdateAccountRequest updateAccountRequest) {
        webClient
                .post()
                .uri("/{accountNumber}/transaction", accountNumber)
                .bodyValue(updateAccountRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3);
    }


    /**
     * Validate if account exist and is in ACTIVE state
     *
     * @param accountNumber customer account number
     * @return boolean value
     */
    @Override
    public boolean validateAccount(String accountNumber) {
        return Boolean.TRUE.equals(webClient
                .post()
                .uri("/{accountNumber}/validate", accountNumber)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        res -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.INVALID_ACCOUNT + accountNumber)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        res -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server error")))
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false)
                .block());
    }
}
