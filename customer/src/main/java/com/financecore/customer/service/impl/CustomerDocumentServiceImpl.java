package com.financecore.customer.service.impl;

import com.financecore.customer.dto.response.CustomerDocumentResponse;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.CustomerDocument;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.mapper.CustomerMapper;
import com.financecore.customer.repository.CustomerDocumentRepository;
import com.financecore.customer.service.CustomerDocumentService;
import com.financecore.customer.service.CustomerService;
import com.financecore.customer.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Implementation class for {@code CustomerDocumentService}
 *
 * @author Roshan
 */
@Service
@Slf4j
public class CustomerDocumentServiceImpl implements CustomerDocumentService {

    /**
     * operations related to customer
     */
    private final CustomerService customerService;

    /**
     * Repository class for {@link CustomerDocument}
     */
    private final CustomerDocumentRepository customerDocumentRepository;

    /**
     * Utility class for enums
     */
    private final EnumUtil enumUtil;

    /**
     * Directory
     */
    @Value("${fc.config.file.upload-dir}")
    private String uploadDir;

    public CustomerDocumentServiceImpl(CustomerService customerService, CustomerDocumentRepository customerDocumentRepository,
                                       EnumUtil enumUtil) {
        this.customerService = customerService;
        this.customerDocumentRepository = customerDocumentRepository;
        this.enumUtil = enumUtil;
    }

    /**
     * Upload customer documents
     *
     * @param customerNumber customer number
     * @param file           Document
     * @param documentType   document type
     * @param documentNumber document number
     * @return {@code CustomerDocumentResponse}
     */
    @Override
    public CustomerDocumentResponse uploadDocuments(long customerNumber, MultipartFile file, String documentType,
                                                    String documentNumber) {
        Customer customer = customerService.getCustomer(customerNumber);
        String fileName = String.valueOf(customerNumber) + System.currentTimeMillis();
        String errorMessage = "Something went wrong. Please try again later....";

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Error creating uploads directory", e.getCause());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }
        }

        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error happened while copying file to directory.", e.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
        DocumentType customerDocumentType = enumUtil.getSafeDocumentType(documentType);
        CustomerDocument customerDocument = CustomerMapper.mapToCustomerDocument(customer, customerDocumentType, documentNumber, fileName, filePath.toString());
        CustomerDocument savedDocument = customerDocumentRepository.save(customerDocument);
        return CustomerMapper.mapToCustomerDocumentResponse(savedDocument);
    }
}
