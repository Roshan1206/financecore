package com.financecore.transaction.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfTransferRequest {

    @NotNull(message = "Amount cannot be empty")
    @DecimalMin(value = "1.00", message = "Amount should be min of 1.00")
    @DecimalMax(value = "9999999.99", message = "Amount cannot exceed 9999999.99")
    @Positive(message = "amount cannot be negative")
    private BigDecimal amount;

    @NotEmpty(message = "Channel is required")
    private String channel;
}
