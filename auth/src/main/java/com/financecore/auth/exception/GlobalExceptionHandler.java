package com.financecore.auth.exception;

import com.financecore.auth.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Handle all exceptions globally
 *
 * @author Roshan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catch ResponseStatusException and handle it gracefully
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception, WebRequest request) {
        log.error("Entered global error handler");
        ErrorResponse response = new ErrorResponse(
                request.getDescription(false),
                exception.getStatusCode().value(),
                exception.getReason(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}