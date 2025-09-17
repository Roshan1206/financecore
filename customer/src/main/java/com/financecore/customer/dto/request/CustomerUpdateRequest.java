package com.financecore.customer.dto.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * CustomerUpdateRequest DTO for customers with required field
 *
 * @author Roshan
 */
@Data
public class CustomerUpdateRequest {
    private String email;
    private String phoneNumber;
}
