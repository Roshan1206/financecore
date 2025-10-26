package com.financecore.auth.dto.response;

import lombok.Data;

import java.util.List;

/**
 * DTO class for Registered clients info
 *
 * @author Roshan
 */
@Data
public class ClientResponse {

    private String clientId;
    private String clientName;
    private String tokenFormat;
    private List<String> redirectUris;
    private List<String> scopes;
    private List<String> grantTypes;
}