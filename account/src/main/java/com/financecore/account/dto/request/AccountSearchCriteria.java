package com.financecore.account.dto.request;

import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.entity.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO class for filtering of accounts.
 *
 * @author Roshan
 */
@Data
public class AccountSearchCriteria {
    private AccountStatus accountStatus;
    private ProductType productType;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String customerId;
}
