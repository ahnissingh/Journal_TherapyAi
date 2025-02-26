package com.ahnis.journalai.exception.custom;

public class JournalNotFoundException extends RuntimeException {
    public JournalNotFoundException(String string) {
        super(string);
    }
}
