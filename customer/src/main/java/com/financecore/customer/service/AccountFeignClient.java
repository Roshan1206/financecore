package com.financecore.customer.service;

import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accounts", path = "/api/v1/accounts")
public interface AccountFeignClient {

    /**
     * Get all accounts for any customer
     */
    @GetMapping("/customer/{customerId}")
    ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(@PathVariable String customerId);
}
