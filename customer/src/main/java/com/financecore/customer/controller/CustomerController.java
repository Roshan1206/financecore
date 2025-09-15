package com.financecore.customer.controller;

import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.service.CustomerService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

//    GET /api/v1/customers
//- Description: Retrieve paginated list of customers with filtering
//- Query Params: page, size, status, customerType, riskProfile, email, phone, accountNumber
    @GetMapping
    public ResponseEntity<PageResponse<CustomersResponse>> getCustomers(@RequestParam(required = false) Status status,
                                                                        @RequestParam(required = false) CustomerType customerType,
                                                                        @RequestParam(required = false) RiskProfile riskProfile,
                                                                        @RequestParam(required = false) String email,
                                                                        @RequestParam(required = false) String phoneNumber,
                                                                        @RequestParam(required = false) String accountNumber,
                                                                        @RequestParam(defaultValue = "0") @Min(0) int pageNumber,
                                                                        @RequestParam(defaultValue = "20") @Min(1) int pageSize,
                                                                        @RequestParam(defaultValue = "id") String sortBy,
                                                                        @RequestParam(defaultValue = "true") boolean asc){
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        PageResponse<CustomersResponse> response = customerService.getCustomers(status, customerType, riskProfile, email, phoneNumber, accountNumber, pageable);
        return ResponseEntity.ok(response);
    }

//
//    GET /api/v1/customers/{customerId}
//- Description: Get detailed customer information including addresses and documents
//
//    POST /api/v1/customers
//- Description: Create new customer profile with KYC initiation
//
//    PUT /api/v1/customers/{customerId}
//- Description: Update customer information and trigger re-verification if needed
//
//    GET /api/v1/customers/{customerId}/accounts
//- Description: Get all accounts associated with a customer (calls Account Service)
//
//    GET /api/v1/customers/search
//- Description: Advanced customer search with complex criteria
//- Query Params: email, phone, accountNumber
//
//    POST /api/v1/customers/{customerId}/documents
//- Description: Upload customer documents for verification
//
//    GET /api/v1/customers/kyc/pending
//- Description: Get customers pending KYC verification
//
//    PUT /api/v1/customers/{customerId}/kyc-status
//- Description: Update KYC verification status
//
//    POST /api/v1/customers/{customerId}/validate
//- Description: Validate customer exists (for inter-service calls)
}
