package com.financecore.transaction.feign;

import com.financecore.transaction.config.feign.AccountServiceFeignConfig;
import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client interface for accounts
 *
 * @author Roshan
 */
@FeignClient(name = "account", path = "/api/v1/accounts", configuration = AccountServiceFeignConfig.class)
public interface AccountFeignClient {

    /**
     * Get account balance
     *
     * @param accountNumber customer account number
     */
    @GetMapping("/{accountNumber}/balance")
    ResponseEntity<BalanceResponse> getAccountBalance(@PathVariable String accountNumber);


    /**
     * Update customer account balance
     */
    @PostMapping("/{accountNumber}/transaction")
    void updateAccountBalance(@PathVariable String accountNumber,
                              @RequestBody UpdateAccountRequest updateAccountRequest);

    /**
     * Validate if account exist and is in ACTIVE state
     */
    @PostMapping("/{accountNumber}/validate")
    boolean validateAccount(@PathVariable String accountNumber);
}
