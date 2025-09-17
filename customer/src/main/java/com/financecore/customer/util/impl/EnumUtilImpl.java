package com.financecore.customer.util.impl;

import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.util.EnumUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Utility implementation class for converting String to desired Enum
 *
 * @author Roshan
 */
@Component
public class EnumUtilImpl implements EnumUtil {

    /**
     * get {@code AddressType} Enum value for address
     *
     * @param addressType address type
     * @return AddressType if valid
     */
    @Override
    public Optional<AddressType> getSafeAddressType(String addressType) {
        try {
            return Optional.of(AddressType.valueOf(addressType.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use  HOME, WORK, or MAILING in addressType");
        }
    }


    /**
     * get {@code CustomerType} Enum value for address
     *
     * @param customerType customer type
     * @return CustomerType if valid
     */
    @Override
    public Optional<CustomerType> getSafeCustomerType(String customerType) {
        try {
            return Optional.of(CustomerType.valueOf(customerType.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use INDIVIDUAL OR BUSINESS in customerType");
        }
    }


    /**
     * get {@code DocumentType} Enum value for address
     *
     * @param documentType document type
     * @return DocumentType if valid
     */
    @Override
    public Optional<DocumentType> getSafeDocumentType(String documentType) {
        try {
            return Optional.of(DocumentType.valueOf(documentType.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use PASSPORT, DRIVING_LICENSE, UTILITY_BILL, AADHAR_CARD in documentType");
        }
    }
}
