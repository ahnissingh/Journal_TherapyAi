package com.ahnis.journalai.user.exception;

public class UserNotSubscribedException extends RuntimeException {
    public UserNotSubscribedException(String message) {
        super(message);
    }
}
