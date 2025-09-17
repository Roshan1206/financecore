package com.financecore.customer.dto.request;

import lombok.Data;

/**
 * AddressRequest DTO for customers with required field
 *
 * @author Roshan
 */
@Data
public class AddressRequest {
    private String addressType;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
