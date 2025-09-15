package com.financecore.account.exception;

import com.financecore.account.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handle exception globally and send ErrorResponse with exception.
 *
 * @author Roshan
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResponseStatusException.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception, WebRequest request){
        ErrorResponse response = new ErrorResponse(
                request.getDescription(false),
                exception.getStatusCode(),
                exception.getReason()
        );
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
