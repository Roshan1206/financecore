package com.financecore.customer.controller;

import com.financecore.customer.dto.request.CustomerRegistrationRequest;
import com.financecore.customer.dto.request.CustomerUpdateRequest;
import com.financecore.customer.dto.response.CustomerDocumentResponse;
import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.service.CustomerService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller class for managing customer via REST APIs.
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    /**
     * Interface for managing customer operations
     */
    private final CustomerService customerService;


    /**
     * Injecting required dependency
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieve paginated list of customers with filtering <br>
     * Query Params: page, size, status, customerType, riskProfile, email, phone, accountNumber
     */
    @GetMapping
    public ResponseEntity<PageResponse<CustomersResponse>> getCustomers(@RequestParam(required = false) Status kycStatus,
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
        PageResponse<CustomersResponse> response = customerService.getCustomers(kycStatus, customerType, riskProfile, email, phoneNumber, accountNumber, pageable);
        return ResponseEntity.ok(response);
    }


    /**
     * Get detailed customer information including addresses and documents
     */
    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerInfoResponse> getCustomerInfo(@PathVariable String customerNumber){
        return ResponseEntity.ok(customerService.getCustomerInfo(customerNumber));
    }


    /**
     * Create new customer profile with KYC initiation
     */
    @PostMapping
    public ResponseEntity<CustomerInfoResponse> createCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRegistrationRequest));
    }


    /**
     * Update customer information <br>
     * TODO: trigger re-verification if needed
     */
    @PutMapping("/{customerNumber}")
    public ResponseEntity<String> updateCustomerInfo(@PathVariable String customerNumber,
                                                     @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        String message = customerService.updateCustomer(customerNumber, customerUpdateRequest);
        return ResponseEntity.ok(message);
    }
//
//    GET /api/v1/customers/{customerId}/accounts
//- Description: Get all accounts associated with a customer (calls Account Service)

    /**
     * Upload customer documents for verification
     */
    @PostMapping("/{customerNumber}/documents")
    public ResponseEntity<CustomerDocumentResponse> uploadDocuments(@PathVariable String customerNumber,
                                                                    @RequestParam("file") MultipartFile file,
                                                                    @RequestParam DocumentType documentType,
                                                                    @RequestParam String documentNumber){
        CustomerDocumentResponse response = customerService.uploadDocuments(customerNumber, file, documentType, documentNumber);
        return ResponseEntity.ok(response);
    }



    /**
     * Update KYC verification status
     */
    @PutMapping("/{customerNumber}/kyc-status")
    public ResponseEntity<String> updateKycStatus(@PathVariable String customerNumber){
        String message = customerService.updateCustomerKyc(customerNumber);
        return ResponseEntity.ok(message);
    }
//
//    POST /api/v1/customers/{customerId}/validate
//- Description: Validate customer exists (for inter-service calls)
}
