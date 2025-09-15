package com.financecore.customer.service;

import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import org.springframework.data.domain.Pageable;

/**
 * Interface responsible for managing customers.
 *
 * @author Roshan
 */
public interface CustomerService {

    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param status Customer KYC Status
     * @param customerType Customer type
     * @param riskProfile Risk profiles
     * @param email Customer email
     * @param phoneNumber Customer phone number
     * @param accountNumber Customer account number
     * @param pageable Paging
     */
    PageResponse<CustomersResponse> getCustomers(Status status, CustomerType customerType, RiskProfile riskProfile,
                                                 String email, String phoneNumber, String accountNumber, Pageable pageable);
}
