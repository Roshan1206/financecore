package com.financecore.account.controller;

import com.financecore.account.dto.request.AccountSearchCriteria;
import com.financecore.account.dto.request.CreateAccountRequest;
import com.financecore.account.dto.request.UpdateAccountRequest;
import com.financecore.account.dto.response.AccountsResponse;
import com.financecore.account.dto.response.BalanceResponse;
import com.financecore.account.dto.response.PageResponse;
import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Rest Controller class for managing accounts.
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1")
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
     * Get paginated accounts with filtering.
     *
     * @param searchCriteria accounts filter
     * @param pageable paging
     * @return paginated accounts based on filtering.
     */
    @GetMapping
    public ResponseEntity<PageResponse<AccountsResponse>> getAccounts(@ModelAttribute AccountSearchCriteria searchCriteria,
                                                                      @PageableDefault(page = 0, size = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){
        if (searchCriteria.getToDate() != null && searchCriteria.getFromDate() == null) {
            log.error("fromDate is missing. It is required for toDate");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate is missing. It is required for toDate");
        }
        PageResponse<AccountsResponse> response = accountService.getAccounts(searchCriteria, pageable);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/{accountId}")
//    public ResponseEntity<> getDetailedAccountDetail(@PathVariable String accountId){
//
//    }


    /**
     * Create new account.
     *
     * @param request required details
     * @return created account details
     */
    @PostMapping
    public ResponseEntity<AccountsResponse> createAccount(@RequestBody CreateAccountRequest request){
        AccountsResponse response = accountService.createNewAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Get all accounts for any customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(@PathVariable String customerId) {
        Sort sort = Sort.by("customerId").ascending();
        Pageable pageable = PageRequest.of(1, 10, sort);
        PageResponse<AccountsResponse> response = accountService.getCustomerAccounts(customerId, pageable);
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


    /**
     * Update account balance. Called using transaction service
     */
    @PostMapping("/{accountNumber}/transaction")
    public void updateAccountBalance(@PathVariable String accountNumber,
                                     @RequestBody UpdateAccountRequest updateAccountRequest){
        accountService.updateAccountBalance(accountNumber, updateAccountRequest);
    }


    /**
     * Validate if account exist and is in ACTIVE state
     */
    @PostMapping("/{accountNumber}/validate")
    public boolean validateAccount(@PathVariable String accountNumber){
        return accountService.isAccountValid(accountNumber);
    }
}
