package com.financecore.customer.service;

import com.financecore.customer.dto.response.CustomerDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class for managing Customer's documents.
 *
 * @author Roshan
 */
public interface CustomerDocumentService {

    /**
     * Upload customer documents
     *
     * @param customerNumber customer number
     * @param file Document
     * @param documentType document type
     * @param documentNumber document number
     *
     * @return {@code CustomerDocumentResponse}
     */
    CustomerDocumentResponse uploadDocuments(long customerNumber, MultipartFile file, String documentType, String documentNumber);
}
