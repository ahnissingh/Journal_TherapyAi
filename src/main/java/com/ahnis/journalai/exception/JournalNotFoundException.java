package com.ahnis.journalai.exception;

public class JournalNotFoundException extends RuntimeException {
    public JournalNotFoundException(String string) {
        super(string);
    }
}
