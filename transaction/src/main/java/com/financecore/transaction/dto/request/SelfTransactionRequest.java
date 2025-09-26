package com.financecore.transaction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfTransactionRequest {

    private String accountNumber;
    private BigDecimal amount;
}
