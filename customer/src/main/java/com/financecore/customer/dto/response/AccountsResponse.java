package com.financecore.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Accounts response for sending only required data
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsResponse {
    private String accountNumber;
    private long customerId;
    private String productName;
    private String productType;
    private String accountStatus;
    private LocalDateTime openedAt;
    private BigDecimal balance;
    private BigDecimal availableBalance;
}