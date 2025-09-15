package com.financecore.transaction.mapper;

import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.entity.Transaction;

/**
 * Mapper class Transactions.
 *
 * @author Roshan
 */
public class TransactionMapper {

    /**
     * Map to Transaction response for user
     */
    public static TransactionResponse mapToTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .fromAccountId(transaction.getFromAccountId())
                .toAccountId(transaction.getToAccountId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .currencyCode(transaction.getCurrencyCode())
                .description(transaction.getDescription())
                .category(transaction.getCategory().getParentCategory().toString())
                .status(transaction.getStatus())
                .dateTime(transaction.getDateTime())
                .date(transaction.getDate())
                .channel(transaction.getChannel())
                .referenceNumber(transaction.getReferenceNumber())
                .merchantInfo(transaction.getMerchantInfo())
                .location(transaction.getLocation())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
