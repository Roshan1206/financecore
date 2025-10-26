package com.financecore.auth.exception;

import com.financecore.auth.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Handle all exceptions globally
 *
 * @author Roshan
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catch ResponseStatusException and handle it gracefully
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ErrorResponse handleResponseStatusException(ResponseStatusException exception, WebRequest request) {
        return new ErrorResponse(
                request.getDescription(false),
                exception.getStatusCode().value(),
                exception.getReason(),
                exception.getMessage(),
                LocalDateTime.now()
        );
    }
}