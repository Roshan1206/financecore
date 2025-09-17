package com.financecore.customer.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerUpdateRequest {
    private String email;
    private String phoneNumber;
}
