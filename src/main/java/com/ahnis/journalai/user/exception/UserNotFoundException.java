package com.ahnis.journalai.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message, String userId) {
        super(message + userId);
    }
}
