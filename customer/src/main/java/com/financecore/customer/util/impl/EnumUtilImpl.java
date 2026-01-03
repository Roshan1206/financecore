package com.financecore.customer.util.impl;

import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.util.EnumUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
     * @return AddressType
     */
    @Override
    public AddressType getSafeAddressType(String addressType) {
        String address = addressType.toUpperCase();
        return switch (address) {
            case "HOME" -> AddressType.HOME;
            case "WORK" -> AddressType.WORK;
            case "MAILING" -> AddressType.MAILING;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use  HOME, WORK, or MAILING in addressType");
        };
    }


    /**
     * get {@code CustomerType} Enum value for address
     *
     * @param customerType customer type
     * @return CustomerType if valid
     */
    @Override
    public CustomerType getSafeCustomerType(String customerType) {
        return switch (customerType.toUpperCase()) {
            case "INDIVIDUAL" -> CustomerType.INDIVIDUAL;
            case "BUSINESS" -> CustomerType.BUSINESS;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use INDIVIDUAL OR BUSINESS in customerType");
        };
    }


    /**
     * get {@code DocumentType} Enum value for address
     *
     * @param documentType document type
     * @return DocumentType if valid
     */
    @Override
    public DocumentType getSafeDocumentType(String documentType) {
        return switch (documentType.toUpperCase()) {
            case "PASSPORT" -> DocumentType.PASSPORT;
            case "DRIVING_LICENSE" -> DocumentType.DRIVING_LICENSE;
            case "UTILITY_BILL" -> DocumentType.UTILITY_BILL;
            case "AADHAR_CARD" -> DocumentType.AADHAR_CARD;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please use PASSPORT, DRIVING_LICENSE, UTILITY_BILL, AADHAR_CARD in documentType");
        };
    }
}
