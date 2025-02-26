package com.ahnis.journalai.exception.custom;

// EmailAlreadyExistsException.java
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
