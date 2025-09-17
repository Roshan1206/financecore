package com.financecore.customer.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String customerType;
    private LocalDate dateOfBirth;
    private AddressRequest address;
    private CustomerDocumentRequest customerDocument;
}
