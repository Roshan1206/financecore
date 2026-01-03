package com.financecore.account.service.communication;

import com.financecore.account.config.feign.CustomerFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Feign client for communication to customer service
 *
 * @author Roshan
 */
@FeignClient(name = "customer", path = "/api/v1", configuration = CustomerFeignConfig.class)
public interface CustomerFeignClient {

    /**
     * Validate customer using customer number and send customer id
     */
    @PostMapping("/{customerNumber}/validate")
    public String validateCustomer(@PathVariable long customerNumber);
}
