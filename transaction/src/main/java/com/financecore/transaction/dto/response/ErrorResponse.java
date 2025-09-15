package com.financecore.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Custom Error response to be sent back when something went wrong.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String apiPath;
    private int statusValue;
    private String statusReason;
    private String message;
    private LocalDateTime time;
}
