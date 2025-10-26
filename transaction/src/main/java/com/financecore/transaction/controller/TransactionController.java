package com.financecore.transaction.controller;

import com.financecore.transaction.dto.request.SelfTransferRequest;
import com.financecore.transaction.dto.request.TransferRequest;
import com.financecore.transaction.dto.response.PageResponse;
import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.dto.response.TransferResponse;
import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
import com.financecore.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction controller for transaction related operations.
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    /**
     * Interface for transaction service to perform operations
     */
    private final TransactionService transactionService;

    /**
     * Injecting required dependency via constructor injection
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get paginated transactions with complex filtering
     */
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(@RequestParam(required = false) String accountId,
                                                                             @RequestParam(required = false) LocalDate fromDate,
                                                                             @RequestParam(required = false) LocalDate toDate,
                                                                             @RequestParam(required = false) BigDecimal amount,
                                                                             @RequestParam(required = false) TransactionType type,
                                                                             @RequestParam(required = false) Status status,
                                                                             @RequestParam(required = false) Channel channel,
                                                                             @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
                                                                             @RequestParam(defaultValue = "20") @Min(1) int pageSize,
                                                                             @RequestParam(defaultValue = "true") boolean sortOrder,
                                                                             @RequestParam(defaultValue = "id") String sortBy){
        Sort sort = sortOrder ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        PageResponse<TransactionResponse> responses = transactionService.getTransactions(accountId, fromDate, toDate, amount, type, status, channel, pageable);
        return ResponseEntity.ok(responses);
    }


    /**
     * Get detailed transaction information with full audit trail
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getDetailedTransaction(@PathVariable long transactionId){
        return ResponseEntity.ok(transactionService.getDetailedTransaction(transactionId));
    }


    /**
     * Generate and retrieve account statements
     */
    @GetMapping("{accountNumber}/statements")
    public ResponseEntity<PageResponse<TransactionResponse>> getAccountTransactions(@PathVariable String accountNumber,
                                                                                    @RequestParam(required = false) LocalDate fromDate,
                                                                                    @RequestParam(required = false) LocalDate toDate) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(1, 10, sort);
        PageResponse<TransactionResponse> responses = transactionService.getTransactions(accountNumber, fromDate, toDate, null, null, null, null, pageable);
        return ResponseEntity.ok(responses);
    }



    /**
     * Create withdrawal transaction.
     *
     * @param accountNumber user account number
     * @param selfTransferRequest info required to create withdrawal
     */
    @PostMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<TransferResponse> createWithdrawal(@PathVariable String accountNumber,
                                                             @RequestBody @Valid SelfTransferRequest selfTransferRequest){
        TransferResponse response = transactionService.createWithdrawal(accountNumber, selfTransferRequest);
        return ResponseEntity.ok(response);
    }


    /**
     * Create deposit transaction.
     *
     * @param accountNumber user account number
     * @param selfTransferRequest info required to create deposit
     */
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<TransferResponse> createDeposit(@PathVariable String accountNumber,
                                                          @RequestBody @Valid SelfTransferRequest selfTransferRequest){
        TransferResponse response = transactionService.createDeposit(accountNumber, selfTransferRequest);
        return ResponseEntity.ok(response);
    }


    /**
     * Create transfer transaction
     *
     * @param accountNumber user account number
     * @param transferRequest info required to create transfer
     */
    @PostMapping("/{accountNumber}/transfer")
    public ResponseEntity<TransferResponse> createTransfer(@PathVariable String accountNumber,
                                                           @RequestBody @Valid TransferRequest transferRequest){
        TransferResponse response = transactionService.createTransfer(accountNumber, transferRequest);
        return ResponseEntity.ok(response);
    }
}
