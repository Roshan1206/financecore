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
public class AccountsResponse {
    private String accountNumber;
    private String customerId;
    private String productName;
    private String productType;
    private String accountStatus;
    private LocalDateTime openedAt;
    private BigDecimal balance;
    private BigDecimal availableBalance;

    public AccountsResponse() {}

    public AccountsResponse(String accountNumber, String customerId, String productName, ProductType productType, AccountStatus accountStatus,
                            LocalDateTime openedAt, BigDecimal balance, BigDecimal availableBalance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.productName = productName;
        this.productType = productType.toString();
        this.accountStatus = accountStatus.toString();
        this.openedAt = openedAt;
        this.balance = balance;
        this.availableBalance = availableBalance;
    }
}
