package com.financecore.account.dto.response;

import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.entity.enums.ProductType;
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
    private ProductType productType;
    private AccountStatus accountStatus;
    private LocalDateTime openedAt;
    private BigDecimal balance;
    private BigDecimal availableBalance;
}
