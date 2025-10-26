package com.financecore.auth.controller;

import com.financecore.auth.dto.request.ClientRegistrationRequest;
import com.financecore.auth.dto.response.ClientResponse;
import com.financecore.auth.service.OAuth2ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for client
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/client")
public class OAuth2ClientController {

    /**
     * Service class for client operations.
     */
    private final OAuth2ClientService oAuth2ClientService;

    /**
     * Injecting required dependency via constructor injection.
     */
    public OAuth2ClientController(OAuth2ClientService oAuth2ClientService) {
        this.oAuth2ClientService = oAuth2ClientService;
    }


    /**
     * Register new client for services
     */
    @PostMapping("/register")
    public ResponseEntity<ClientResponse> registerClient(@RequestBody ClientRegistrationRequest clientRegistrationRequest) {
        ClientResponse response = oAuth2ClientService.registerClient(clientRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
