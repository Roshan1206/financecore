package com.financecore.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

/**
 * Custom error response class for sending exceptions
 *
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String path;
    private int statusCode;
    private String statusReason;
    private String exception;
    private LocalDateTime time;

    public ErrorResponse (String path, HttpStatusCode status, String exception) {
        this.path = path;
        this.statusCode = status.value();
        this.statusReason = status.toString().substring(4);
        this.exception = exception;
        this.time = LocalDateTime.now();
    }
}
