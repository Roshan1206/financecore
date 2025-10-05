package com.financecore.transaction.mapper;

import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.dto.response.TransferResponse;
import com.financecore.transaction.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper class Transactions.
 *
 * @author Roshan
 */
@Slf4j
public class TransactionMapper {

    /**
     * Map to Transaction response for user
     */
    public static TransactionResponse mapToTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .fromAccountNumber(transaction.getFromAccountId())
                .toAccountNumber(transaction.getToAccountId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .currencyCode(transaction.getCurrencyCode())
                .description(transaction.getDescription())
                .category(transaction.getCategory().getName())
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

    public static TransferResponse createTransferResponse(Transaction transaction) {
        TransferResponse response = new TransferResponse();
        response.setTransactionType(transaction.getType().toString());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setAccountNumber(transaction.getToAccountId());
        response.setStatus(transaction.getStatus().toString());
        response.setAmount(transaction.getAmount());
        response.setCreatedAt(transaction.getCreatedAt());

        if (transaction.getUpdatedAt() != null) {
            response.setUpdatedAt(transaction.getUpdatedAt());
        }else {
            response.setCreatedAt(transaction.getCreatedAt());
        }
        log.info("Transaction completed successfully.");
        return response;
    }
}
