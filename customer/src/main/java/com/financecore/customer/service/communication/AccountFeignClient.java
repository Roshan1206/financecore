package com.financecore.customer.service.communication;

import com.financecore.customer.config.feign.AccountServiceFeignConfig;
import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for accounts
 *
 * @author Roshan
 */
@FeignClient(name = "account", path = "/api/v1", configuration = AccountServiceFeignConfig.class)
public interface AccountFeignClient {

    /**
     * Get all accounts for any customer
     */
    @GetMapping("/customer/{customerId}")
    ResponseEntity<PageResponse<AccountsResponse>> getCustomerAccounts(@PathVariable String customerId);
}
