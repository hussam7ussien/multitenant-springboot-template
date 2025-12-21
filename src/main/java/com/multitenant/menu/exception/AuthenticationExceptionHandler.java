package com.multitenant.menu.exception;

import com.multitenant.menu.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class AuthenticationExceptionHandler {

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtpException(InvalidOtpException e) {
        log.warn("Invalid OTP: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setError("INVALID_OTP");
        error.setMessage(e.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InvalidSessionTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSessionTokenException(InvalidSessionTokenException e) {
        log.warn("Invalid session token: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setError("INVALID_SESSION_TOKEN");
        error.setMessage(e.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.warn("Invalid refresh token: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setError("INVALID_REFRESH_TOKEN");
        error.setMessage(e.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(PhoneNumberRequiredException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberRequiredException(PhoneNumberRequiredException e) {
        log.warn("Phone number required: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setError("PHONE_NUMBER_REQUIRED");
        error.setMessage(e.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

