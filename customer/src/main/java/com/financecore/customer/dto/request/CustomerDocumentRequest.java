package com.financecore.customer.dto.request;

import lombok.Data;

@Data
public class CustomerDocumentRequest {
    private String documentType;
    private String documentNumber;
}
