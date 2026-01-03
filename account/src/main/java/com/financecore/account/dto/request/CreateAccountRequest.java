package com.financecore.account.dto.request;

import lombok.Data;

/**
 * DTO class for creating account.
 *
 * @author Roshan
 */
@Data
public class CreateAccountRequest {

    private String customerId;
    private String productName;
}
