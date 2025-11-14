package com.financecore.account.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetailedAccountResponse {

    private String accountNumber;
    private long customerId;
    private String productName;
    private String productType;
    private String accountStatus;
    private LocalDateTime openedAt;
    private BigDecimal balance;
    private BigDecimal availableBalance;
}
