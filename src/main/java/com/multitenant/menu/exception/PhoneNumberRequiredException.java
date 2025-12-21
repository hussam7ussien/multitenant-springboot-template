package com.multitenant.menu.exception;

public class PhoneNumberRequiredException extends RuntimeException {
    public PhoneNumberRequiredException(String message) {
        super(message);
    }
}

