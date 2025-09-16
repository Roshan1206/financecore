package com.financecore.customer.dto.response;

import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CustomerDocumentResponse with required information
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocumentResponse {

    private DocumentType documentType;
    private String documentNumber;
    private String filePath;
    private Status verificationStatus;
    private LocalDateTime uploadedAt;
}
