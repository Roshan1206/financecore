package com.financecore.customer.service;

import com.financecore.customer.dto.request.CustomerRegistrationRequest;
import com.financecore.customer.dto.request.CustomerUpdateRequest;
import com.financecore.customer.dto.response.CustomerInfoResponse;
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


    /**
     * Get detailed information for customer.
     *
     * @param customerNumber Customer number
     * @return customer information
     */
    CustomerInfoResponse getCustomerInfo(String customerNumber);


    /**
     * Create new customer profile with KYC initiation
     *
     * @param customerRegistrationRequest Customer details
     * @return Detailed customer info
     */
    CustomerInfoResponse createCustomer(CustomerRegistrationRequest customerRegistrationRequest);


    /**
     * Update customer information
     *
     * @param customerNumber customer number
     * @param customerUpdateRequest Update info
     *
     * @return Message
     */
    String updateCustomer(String customerNumber, CustomerUpdateRequest customerUpdateRequest);


    /**
     * Update KYC verification status
     *
     * @param customerNumber customer number
     *
     * @return Message
     */
    String updateCustomerKyc(String customerNumber);
}
