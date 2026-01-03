package com.financecore.customer.dto.response;

import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * CustomerInfoResponse DTO for customers with required field
 *
 * @author Roshan
 */
@Data
@Builder
public class CustomerInfoResponse {
    private String customerID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private CustomerType customerType;
    private Status kycStatus;
    private RiskProfile riskProfile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private Set<AddressResponse> addresses;
    private List<CustomerDocumentResponse> customerDocuments;
}
