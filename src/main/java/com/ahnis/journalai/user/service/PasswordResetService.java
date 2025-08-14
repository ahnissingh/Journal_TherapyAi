package com.ahnis.journalai.user.service;

public interface PasswordResetService {
    // Generate a token and send an email
    void sendPasswordResetEmail(String userEmail);

    // Reset the password
    void resetPassword(String token, String newPassword);
}
