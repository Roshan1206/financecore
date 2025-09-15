package com.financecore.account.controller;

import com.financecore.account.dto.response.AccountsResponse;
import com.financecore.account.dto.response.BalanceResponse;
import com.financecore.account.dto.response.PageResponse;
import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.entity.enums.ProductType;
import com.financecore.account.service.AccountService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rest Controller class for managing accounts.
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountController {

    /**
     * Service interface for handling managing accounts.
     */
    private final AccountService accountService;


    /**
     * Injecting required dependency via constructor injection.
     */
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    /**
     * Get paginated accounts with filtering by status, type, balance range, created date
     */
    @GetMapping
    public ResponseEntity<PageResponse<AccountsResponse>> getAccounts(@RequestParam(required = false) AccountStatus accountStatus,
                                                                      @RequestParam(required = false) ProductType productType,
                                                                      @RequestParam(required = false) BigDecimal minAmount,
                                                                      @RequestParam(required = false) BigDecimal maxAmount,
                                                                      @RequestParam(required = false) LocalDate fromDate,
                                                                      @RequestParam(required = false) LocalDate toDate,
                                                                      @RequestParam(required = false) String customerId,
                                                                      @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
                                                                      @RequestParam(defaultValue = "20") @Min(1) @Max(30) int pageSize,
                                                                      @RequestParam(defaultValue = "id") String sortBy,
                                                                      @RequestParam(defaultValue = "true") boolean asc){
        if (toDate != null && fromDate == null) {
            log.error("fromDate is missing. It is required for toDate");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate is missing. It is required for toDate");
        }
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        PageResponse<AccountsResponse> response = accountService.getAccounts(accountStatus, productType, minAmount, maxAmount, fromDate, toDate, customerId, pageable);
        return ResponseEntity.ok(response);
    }


    /**
     * Update account Status
     */
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<String> updateAccountStatus(@PathVariable String accountNumber, @RequestParam AccountStatus accountStatus){
        String message = accountService.updateAccountStatus(accountNumber, accountStatus);
        return ResponseEntity.ok(message);
    }


    /**
     * Get Account balance for customer
     */
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BalanceResponse> getAccountBalance(@PathVariable String accountNumber){
        BalanceResponse response = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(response);
    }


    /**
     * Suspend Account
     * TODO: Add FREEZE in AccountStatus and implement it later
     */
    @PutMapping("/{accountNumber}/freeze")
    public ResponseEntity<String> freezeAccount(@PathVariable String accountNumber){
        String message = accountService.updateAccountStatus(accountNumber, AccountStatus.SUSPENDED);
        return ResponseEntity.ok(message);
    }
}
