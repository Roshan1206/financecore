package com.financecore.customer.mapper;

import com.financecore.customer.dto.response.AddressResponse;
import com.financecore.customer.dto.response.CustomerDocumentResponse;
import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.entity.Address;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.CustomerDocument;

import java.util.stream.Collectors;

public class CustomerMapper {

    public static CustomerInfoResponse mapToCustomerInfoResponse(Customer customer) {
        return CustomerInfoResponse.builder()
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .customerType(customer.getCustomerType())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .dateOfBirth(customer.getDateOfBirth())
                .kycStatus(customer.getKycStatus())
                .riskProfile(customer.getRiskProfile())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .isActive(customer.isActive())
                .customerDocuments(customer.getCustomerDocuments().stream().map(CustomerMapper::mapToCustomerDocumentResponse).toList())
                .addresses(customer.getAddresses().stream().map(CustomerMapper::mapToAddressResponse).collect(Collectors.toSet()))
                .build();
    }

    public static AddressResponse mapToAddressResponse(Address address){
        AddressResponse response = new AddressResponse();
        response.setAddressType(address.getAddressType());
        response.setStreetAddress(address.getStreetAddress());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setZipCode(address.getZipCode());
        response.setPrimary(address.isPrimary());

        return response;
    }

    public static CustomerDocumentResponse mapToCustomerDocumentResponse(CustomerDocument document) {
        CustomerDocumentResponse response = new CustomerDocumentResponse();
        response.setDocumentNumber(document.getDocumentNumber());
        response.setDocumentType(document.getDocumentType());
        response.setFilePath(document.getFilePath());
        response.setUploadedAt(document.getUploadedAt());
        response.setVerificationStatus(document.getVerificationStatus());

        return response;
    }
}
