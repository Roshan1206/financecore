package com.financecore.transaction.mapper;

import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.entity.Transaction;
import com.financecore.transaction.entity.TransactionCategory;
import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;

import java.math.BigDecimal;

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

    public static Transaction createNewTransaction(long fromAccount,
                                                   long toAccount,
                                                   BigDecimal amount,
                                                   Channel channel,
                                                   TransactionType transactionType,
                                                   TransactionCategory transactionCategory,
                                                   String reference,
                                                   String referenceNumber,
                                                   String description,
                                                   Status status,
                                                   String merchantInfo,
                                                   String location){
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccount);
        transaction.setToAccountId(toAccount);
        transaction.setAmount(amount);
        transaction.setChannel(channel);
        transaction.setType(transactionType);
        transaction.setCategory(transactionCategory);
        transaction.setReference(reference);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setDescription(description);
        transaction.setStatus(status);
        transaction.setMerchantInfo(merchantInfo);
        transaction.setLocation(location);
        return transaction;
    }
}
