package com.ahnis.journalai.exception.custom;


public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid username/email or password");
    }
}
