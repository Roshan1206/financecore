package com.financecore.auth.dto.request;

import lombok.Data;

import java.util.List;

/**
 * DTO class for registering client.
 *
 * @author Roshan
 */
@Data
public class ClientRegistrationRequest {

    private String clientId;
    private String clientName;
    private String clientSecret;
    private String tokenFormat;
    private List<String> redirectUris;
    private List<String> scopes;
    private List<String> grantTypes;
}
