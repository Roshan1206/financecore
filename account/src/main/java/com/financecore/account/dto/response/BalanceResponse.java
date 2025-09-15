package com.financecore.account.dto.response;

import com.financecore.account.entity.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Balance response with required details.
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private String accountNumber;
    private AccountStatus accountStatus;
    private String productName;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal interestRate;
    private BigDecimal minimumBalance;
    private BigDecimal overdraftLimit;
}
