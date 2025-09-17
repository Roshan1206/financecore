package com.financecore.customer.mapper;

import com.financecore.customer.dto.request.AddressRequest;
import com.financecore.customer.dto.request.CustomerRegistrationRequest;
import com.financecore.customer.dto.response.AddressResponse;
import com.financecore.customer.dto.response.CustomerDocumentResponse;
import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.entity.Address;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.CustomerDocument;
import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;

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

    public static Customer mapToCustomer(CustomerRegistrationRequest request, CustomerType customerType){
        return Customer.builder()
                .customerType(customerType)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .kycStatus(Status.PENDING)
                .riskProfile(RiskProfile.LOW)
                .isActive(true)
                .build();
    }

    public static Address mapToAddress(Customer customer, AddressType addressType, AddressRequest addressRequest){
        Address address = new Address();
        address.setCustomer(customer);
        address.setAddressType(addressType);
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setZipCode(addressRequest.getZipCode());
        return address;
    }

    public static CustomerDocument mapToCustomerDocument(Customer customer, DocumentType documentType, String documentNumber){
        CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.setCustomer(customer);
        customerDocument.setDocumentType(documentType);
        customerDocument.setDocumentNumber(documentNumber);
        return customerDocument;
    }
}
