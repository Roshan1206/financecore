package com.financecore.customer.util;

import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;

import java.util.Optional;

public interface EnumUtil {
    Optional<AddressType> getSafeAddressType(String addressType);

    Optional<CustomerType> getSafeCustomerType(String customerType);

    Optional<DocumentType> getSafeDocumentType(String documentType);
}
