package com.ahnis.journalai.exception;


public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid username/email or password");
    }
}
