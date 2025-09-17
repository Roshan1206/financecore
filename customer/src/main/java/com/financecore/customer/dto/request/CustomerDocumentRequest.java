package com.financecore.customer.dto.request;

import lombok.Data;

/**
 * CustomerDocumentRequest DTO for customers with required field
 *
 * @author Roshan
 */
@Data
public class CustomerDocumentRequest {
    private String documentType;
    private String documentNumber;
}
