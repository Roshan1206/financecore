package com.financecore.customer.dto.response;

import com.financecore.customer.entity.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddressResponse DTO for customers with required field
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private AddressType addressType;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isPrimary;
}
