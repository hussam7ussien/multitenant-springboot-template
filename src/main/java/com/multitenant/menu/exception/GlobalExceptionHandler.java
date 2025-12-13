package com.multitenant.menu.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        log.warn("Duplicate email error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("Duplicate Email");
        errorResponse.setMessage("This email address is already registered. Please use a different email or login with your existing account.");
        errorResponse.setErrorCode("DUPLICATE_EMAIL");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        
        String message = "An error occurred while processing your request.";
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        
        // Check if it's a duplicate email constraint violation
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("email")) {
            message = "This email address is already registered. Please use a different email or login with your existing account.";
            errorCode = "DUPLICATE_EMAIL";
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("Database Constraint Violation");
        errorResponse.setMessage(message);
        errorResponse.setErrorCode(errorCode);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.error("Request body parse error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Invalid Request Body");
        errorResponse.setMessage("Failed to parse request body: " + ex.getMostSpecificCause().getMessage());
        errorResponse.setErrorCode("INVALID_REQUEST_BODY");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("Validation failed");
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Validation Error");
        errorResponse.setMessage(message);
        errorResponse.setErrorCode("VALIDATION_ERROR");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("An unexpected error occurred. Please try again later.");
        errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String errorCode;
    }
}
