package com.financecore.customer.dto.response;

import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CustomerResponse DTO for search
 *
 * @author Roshan
 */
@Data
@Builder
public class CustomersResponse {
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private CustomerType customerType;
    private Status kycStatus;
    private RiskProfile riskProfile;
    private LocalDateTime createdAt;
}
