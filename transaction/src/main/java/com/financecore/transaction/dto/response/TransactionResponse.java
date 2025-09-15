package com.financecore.transaction.dto.response;

import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction response for Transaction entity
 *
 * @author Roshan
 */
@Data
@Builder
public class TransactionResponse {

    private long id;
    private String reference;
    private long fromAccountId;
    private long toAccountId;
    private TransactionType type;
    private BigDecimal amount;
    private String currencyCode;
    private String description;
    private String category;
    private Status status;
    private LocalDateTime dateTime;
    private LocalDate date;
    private Channel channel;
    private String referenceNumber;
    private String merchantInfo;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
