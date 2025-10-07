package com.financecore.customer.util;

import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;

import java.util.Optional;

/**
 * Utility interface for converting String to desired Enum
 *
 * @author Roshan
 */
public interface EnumUtil {

    /**
     * get {@code AddressType} Enum value for address
     *
     * @param addressType address type
     * @return AddressType
     */
    AddressType getSafeAddressType(String addressType);


    /**
     * get {@code CustomerType} Enum value for address
     *
     * @param customerType customer type
     * @return CustomerType
     */
    CustomerType getSafeCustomerType(String customerType);


    /**
     * get {@code DocumentType} Enum value for address
     *
     * @param documentType document type
     * @return DocumentType
     */
    DocumentType getSafeDocumentType(String documentType);
}
