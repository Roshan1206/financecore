package com.financecore.transaction.service;

import com.financecore.transaction.dto.request.SelfTransferRequest;
import com.financecore.transaction.dto.request.TransferRequest;
import com.financecore.transaction.dto.response.PageResponse;
import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.dto.response.TransferResponse;
import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface for transactions service
 *
 * @author Roshan
 */
public interface TransactionService {

    /**
     * Get paginated transaction for a particular account
     *
     * @param accountId account from which transactions happened
     * @param fromDate transactions from date
     * @param toDate transactions till date
     * @param amount transaction amount
     * @param type transaction type
     * @param status transaction status
     * @param channel channel from which transaction happened
     * @param pageable paging information
     *
     * @return Page of transactions
     */
    PageResponse<TransactionResponse> getTransactions(String accountId, LocalDate fromDate, LocalDate toDate, BigDecimal amount,
                                                      TransactionType type, Status status, Channel channel, Pageable pageable);


    /**
     * Get detailed transaction information with full audit trail
     *
     * @param transactionId transaction ID
     * @return transaction
     */
    TransactionResponse getDetailedTransaction(long transactionId);


    /**
     * Create withdrawal for self
     *
     * @param accountNumber account number
     * @param selfTransferRequest channel and amount
     */
    TransferResponse createWithdrawal(String accountNumber, SelfTransferRequest selfTransferRequest);


    /**
     * Create deposit for self
     *
     * @param accountNumber account number
     * @param selfTransferRequest channel and amount
     */
    TransferResponse createDeposit(String accountNumber, SelfTransferRequest selfTransferRequest);


    /**
     * Create deposit for self
     *
     * @param accountNumber account number
     * @param transferRequest account number, channel and amount
     */
    TransferResponse createTransfer(String accountNumber, TransferRequest transferRequest);
}
