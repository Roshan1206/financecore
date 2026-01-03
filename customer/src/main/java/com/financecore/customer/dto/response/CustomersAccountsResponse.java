package com.financecore.customer.dto.response;

import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CustomerResponse DTO for search
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomersAccountsResponse {
    private long customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private CustomerType customerType;
    private Status kycStatus;
    private RiskProfile riskProfile;
    private LocalDateTime createdAt;
    private List<AccountsResponse>  accounts;
}